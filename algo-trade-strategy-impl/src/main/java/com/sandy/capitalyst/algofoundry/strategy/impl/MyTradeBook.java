package com.sandy.capitalyst.algofoundry.strategy.impl;

import com.sandy.capitalyst.algofoundry.strategy.impl.util.StringUtil;
import com.sandy.capitalyst.algofoundry.strategy.series.numseries.ConstantSeries;
import com.sandy.capitalyst.algofoundry.strategy.series.numseries.indicator.CrossDownIndicator;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.DayValue;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.dayvalue.OHLCVDayValue;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.TradeSignalEvent;
import com.sandy.capitalyst.algofoundry.strategy.tradebook.BuyTrade;
import com.sandy.capitalyst.algofoundry.strategy.tradebook.SellTrade;
import com.sandy.capitalyst.algofoundry.strategy.tradebook.TradeBook;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyTradeBook extends TradeBook {
    
    private final CrossDownIndicator maxLossIndicator ;
    private final CrossDownIndicator minProfitIndicator ;
    private final ConstantSeries minProfitThreshold ;
    private final ConstantSeries maxLossThreshold ;
    
    private int     buyCooloffDaysLeft     = 0 ;
    private boolean ignoreStopLossTomorrow = false ;
    
    private final MyStrategyConfig config ;
    
    public MyTradeBook( MyStrategyConfig config ) {
        
        super( config ) ;
        
        this.config = config ;
        
        minProfitThreshold = ConstantSeries.of( config.getMinProfitThreshold() ) ;
        maxLossThreshold   = ConstantSeries.of( config.getMaxLossThreshold() ) ;
        
        maxLossIndicator = new CrossDownIndicator( notionalProfitPctSeries, maxLossThreshold ) ;
        minProfitIndicator = new CrossDownIndicator( notionalProfitPctSeries, minProfitThreshold ) ;
    }
    
    @Override
    protected BuyTrade handleBuySignal( TradeSignalEvent te ) {
        
        if( buyCooloffDaysLeft <= 0 ) {
            double investmentQuantum = config.getInvestmentQuantum() ;
            int quantity = (int)(investmentQuantum/te.getClosingPrice()) ;
            if( quantity > 0 ) {
                buyCooloffDaysLeft = config.getBuyCooloffDuration() ;
                ignoreStopLossTomorrow = true ;
                return new BuyTrade( te.getDate(), te.getClosingPrice(), quantity ) ;
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
            return new SellTrade( te.getDate(), te.getClosingPrice(), super.getHoldingQty() ) ;
        }
        return null ;
    }
    
    @Override
    public void handleDayValue( DayValue dayValue ) {
        
        super.handleDayValue( dayValue ) ;
        
        if( dayValue instanceof OHLCVDayValue ohlcv ) {
            
            if( buyCooloffDaysLeft > 0 ) { buyCooloffDaysLeft-- ; }
            
            // If we bought yesterday, don't compute stop loss today
            // since the notional profit % would have dropped significantly.
            if( ignoreStopLossTomorrow ) {
                ignoreStopLossTomorrow = false ;
                return ;
            }
            
            // Don't do stop loss check if there is no holding.
            if( super.getHoldingQty() <= 0 ) return ;
            
            int index = notionalProfitPctSeries.getSize()-1 ;
            
            if( index > 0 ) {
                
                double lastNotionalProfitPct = notionalProfitPctSeries.getValue( index-1 ) ;
                double curNotionalProfitPct  = notionalProfitPctSeries.getValue( index ) ;
                
                boolean minProfitBreached = minProfitIndicator.isSatisfied( index ) ;
                boolean maxLossBreached   = maxLossIndicator.isSatisfied( index ) ;
                
                if( minProfitBreached || maxLossBreached ) {
                    if( minProfitBreached ) {
                        log.info( "Min profit breached." ) ;
                    }
                    else {
                        log.info( "Max loss breached." ) ;
                    }
                    log.debug( "  Last profit % - {}", StringUtil.fmtDbl( lastNotionalProfitPct ) ) ;
                    log.debug( "  Cur profit % - {}", StringUtil.fmtDbl( curNotionalProfitPct ) ) ;
                    processSellTrade( new SellTrade( dayValue.getDate(),
                                        ohlcv.getClose(),
                                        super.getHoldingQty() ) ) ;
                }
            }
        }
    }
}
