package com.sandy.capitalyst.algofoundry.strategy.impl;

import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.DayValue;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.dayvalue.OHLCVDayValue;
import com.sandy.capitalyst.algofoundry.strategy.series.numseries.ConstantSeries;
import com.sandy.capitalyst.algofoundry.strategy.series.numseries.indicator.CrossDownIndicator;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.TradeSignalEvent;
import com.sandy.capitalyst.algofoundry.strategy.tradebook.BuyTrade;
import com.sandy.capitalyst.algofoundry.strategy.tradebook.SellTrade;
import com.sandy.capitalyst.algofoundry.strategy.tradebook.TradeBook;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

import static com.sandy.capitalyst.algofoundry.strategy.impl.util.StringUtil.fmtDate;
import static com.sandy.capitalyst.algofoundry.strategy.impl.util.StringUtil.fmtDbl;

@Slf4j
public class MyTradeBook extends TradeBook {
    
    private CrossDownIndicator sltpCrossDownIndicator = null ;
    private ConstantSeries     sltpSeries             = null ;

    private int buyCooloffDaysLeft = 0 ;
    private int numConsecutiveBuys = 0 ;
    
    private final MyStrategyConfig config ;
    
    public MyTradeBook( MyStrategyConfig config ) {
        super( config ) ;
        this.config = config ;
    }
    
    @Override
    protected BuyTrade handleBuySignal( TradeSignalEvent te ) {
        
        if( buyCooloffDaysLeft <= 0 ) {
            double investmentQuantum = config.getInvestmentQuantum() ;
            int quantity = (int)(investmentQuantum/te.getClosingPrice()) ;
            
            if( quantity > 0 ) {

                // With every successive buy, we reduce the investment amount
                // to ensure we don't pull the average cost high and other reasons.
                double successiveInvestmentTaperPct = config.getSuccessiveInvestmentTaperPct()/100 ;
                double pctLess = 1 - numConsecutiveBuys * successiveInvestmentTaperPct ;
                investmentQuantum *= pctLess ;
                
                // After reducing the investment cost, we check if we have
                // sufficient funds to buy.
                quantity = (int)(investmentQuantum/te.getClosingPrice()) ;
                if( quantity > 0 ) {
                    numConsecutiveBuys++ ;
                    buyCooloffDaysLeft = config.getBuyCooloffDuration() ;
                    return new BuyTrade( te.getDate(), te.getClosingPrice(), quantity ) ;
                }
            }
            else {
                log.debug( "Single share price exceeds {}}. Ignoring buy signal.",
                           config.getInvestmentQuantum() );
            }
        }
        else {
            log.debug( "In cool-off period. Ignoring buy signal." ) ;
        }
        return null ;
    }
    
    @Override
    protected SellTrade handleSellSignal( TradeSignalEvent te ) {
        if( super.getHoldingQty() > 0 ) {
            double sellPrice = te.getClosingPrice() ;
            if( sltpSeries != null ) {
                if( sltpSeries.getConstantValue() > te.getClosingPrice() ) {
                    sellPrice = sltpSeries.getConstantValue() ;
                }
            }
            return createSellTrade( te.getDate(), sellPrice ) ;
        }
        return null ;
    }
    
    @Override
    public void handleDayValue( DayValue dayValue ) {
        
        super.handleDayValue( dayValue ) ;
        
        if( dayValue instanceof OHLCVDayValue ohlcv ) {
            if( buyCooloffDaysLeft > 0 ) { buyCooloffDaysLeft-- ; }
            
            // Don't do stop loss check if there is no holding.
            if( super.getHoldingQty() <= 0 ) return ;
            
            if( config.getSltpCurrentPricePct() >= 0 ) {
                applyStopLossProcessing( ohlcv ) ;
            }
        }
    }
    
    private void applyStopLossProcessing( OHLCVDayValue ohlcv ) {
        
        double currentPrice = ohlcv.getClose() ;
        String curDtStr     = fmtDate( ohlcv.getDate() ) ;
        
        double sltpCurrentPricePct = config.getSltpCurrentPricePct() ;
        
        if( sltpSeries == null ) {
            double sltpTriggerPrice = 0 ;
            
            if( sltpCurrentPricePct >= 0 ) {
                sltpTriggerPrice = (1-sltpCurrentPricePct/100) * currentPrice ;
                sltpSeries = ConstantSeries.of( sltpTriggerPrice ) ;
                sltpCrossDownIndicator = new CrossDownIndicator( cpSeries, sltpSeries ) ;
                log.debug( "{} = Setting SLTP to {}", curDtStr,
                           fmtDbl( sltpSeries.getConstantValue() ) ) ;
            }
        }
        else {
            double currentSltp = sltpSeries.getConstantValue() ;
            double newSltp = (1-sltpCurrentPricePct/100) * currentPrice ;
            
            if( newSltp > currentSltp ) {
                log.debug( "{} - Revising SLTP to {}", curDtStr,
                           fmtDbl( newSltp ) ) ;
                sltpSeries.setConstantValue( newSltp ) ;
            }
            else {
                if( sltpCrossDownIndicator.isSatisfied( cpSeries.getSize()-1 ) ) {
                    log.debug( "{} - SLTP breached at {}", curDtStr,
                               fmtDbl( currentSltp ) ) ;
                    
                    processSellTrade( createSellTrade( ohlcv.getDate(), currentSltp ) ) ;
                }
            }
        }
    }
    
    private SellTrade createSellTrade( Date date, double sellPrice ) {
        
        sltpSeries = null ;
        sltpCrossDownIndicator = null ;
        numConsecutiveBuys = 0 ;
        
        return new SellTrade( date, sellPrice, super.getHoldingQty() ) ;
    }
}
