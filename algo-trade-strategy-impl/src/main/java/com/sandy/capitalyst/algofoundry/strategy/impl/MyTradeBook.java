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

import static com.sandy.capitalyst.algofoundry.strategy.impl.util.StringUtil.fmtDate;
import static com.sandy.capitalyst.algofoundry.strategy.impl.util.StringUtil.fmtDbl;

@Slf4j
public class MyTradeBook extends TradeBook {
    
    private CrossDownIndicator gttCrossDownIndicator = null ;
    private ConstantSeries     gttLimitPriceSeries = null ;

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
                // by 20% to ensure we don't pull the average cost high.
                double pctLess = 1 - ( double )numConsecutiveBuys/10 ;
                
                // After reducing the investment cost, we check if we have
                // sufficient funds to buy.
                investmentQuantum *= pctLess ;
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
            numConsecutiveBuys = 0 ;
            double sellPrice = te.getClosingPrice() ;
            if( gttLimitPriceSeries != null ) {
                if( gttLimitPriceSeries.getConstantValue() > te.getClosingPrice() ) {
                    sellPrice = gttLimitPriceSeries.getConstantValue() ;
                }
            }
            return new SellTrade( te.getDate(), sellPrice, super.getHoldingQty() ) ;
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
            
            double currentPrice = ohlcv.getClose() ;
            
            if( gttLimitPriceSeries == null ) {
                gttLimitPriceSeries = ConstantSeries.of( 0.9 * currentPrice ) ;
                gttCrossDownIndicator = new CrossDownIndicator( cpSeries, gttLimitPriceSeries ) ;
                log.debug( "{} = Setting GTT to {}",
                        fmtDate( ohlcv.getDate() ),
                        fmtDbl( gttLimitPriceSeries.getConstantValue() ) ) ;
            }
            else {
                double gttLimitPrice = gttLimitPriceSeries.getConstantValue() ;
                double newGttLimitPrice = currentPrice * 0.9 ;
                if( newGttLimitPrice > gttLimitPrice ) {
                    log.debug( "{} - Revising GTT to {}",
                            fmtDate( ohlcv.getDate() ),
                            fmtDbl( newGttLimitPrice ) ) ;
                    gttLimitPriceSeries.setConstantValue( newGttLimitPrice ) ;
                }
                else {
                    if( gttCrossDownIndicator.isSatisfied( cpSeries.getSize()-1 ) ) {
                        log.debug( "{} - GTT breached at {}",
                                fmtDate( ohlcv.getDate() ),
                                fmtDbl( gttLimitPrice ) ) ;
                        gttLimitPriceSeries = null ;
                        gttCrossDownIndicator = null ;
                        numConsecutiveBuys = 0 ;
                        processSellTrade( new SellTrade( dayValue.getDate(),
                                            gttLimitPrice,
                                            super.getHoldingQty() ) ) ;
                    }
                }
            }
        }
    }
}
