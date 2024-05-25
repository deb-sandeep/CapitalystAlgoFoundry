package com.sandy.capitalyst.algofoundry.strategy.trade;

import com.sandy.capitalyst.algofoundry.core.numseries.series.ConstantSeries;
import com.sandy.capitalyst.algofoundry.core.numseries.indicator.CrossDownIndicator;
import com.sandy.capitalyst.algofoundry.eodhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.eodhistory.dayvalue.OHLCVDayValue;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.TradeSignalEvent;
import com.sandy.capitalyst.algofoundry.tradebook.BuyTrade;
import com.sandy.capitalyst.algofoundry.tradebook.SellTrade;
import com.sandy.capitalyst.algofoundry.tradebook.TradeBook;
import lombok.extern.slf4j.Slf4j;

import static com.sandy.capitalyst.algofoundry.core.util.StringUtil.fmtDbl;

@Slf4j
public class DefaultTradeBook extends TradeBook {
    
    private final CrossDownIndicator maxLossIndicator ;
    private final CrossDownIndicator minProfitIndicator ;
    private final ConstantSeries minProfitThreshold ;
    private final ConstantSeries maxLossThreshold ;
    
    private int     buyCooloffDaysLeft     = 0 ;
    private boolean ignoreStopLossTomorrow = false ;
    
    public DefaultTradeBook() {
        
        minProfitThreshold = ConstantSeries.of( 10 ) ;
        maxLossThreshold   = ConstantSeries.of( -10 ) ;
        
        maxLossIndicator = new CrossDownIndicator( notionalProfitPctSeries, maxLossThreshold ) ;
        minProfitIndicator = new CrossDownIndicator( notionalProfitPctSeries, minProfitThreshold ) ;
    }
    
    @Override
    protected BuyTrade handleBuySignal( TradeSignalEvent te ) {
        
        if( buyCooloffDaysLeft <= 0 ) {
            double investmentQuantum = 25000 ;
            int quantity = (int)(investmentQuantum/te.getClosingPrice()) ;
            if( quantity > 0 ) {
                buyCooloffDaysLeft = 6 ;
                ignoreStopLossTomorrow = true ;
                return new BuyTrade( te.getDate(), te.getClosingPrice(), quantity ) ;
            }
            else {
                log.debug( "Single share price exceeds 25000. Ignoring buy signal." );
            }
        }
        else {
            log.debug( "In cooloff period. Ignoring buy signal." ) ;
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
    public void handleDayValue( AbstractDayValue dayValue ) {
        
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
                
                if( lastNotionalProfitPct > 10 ) {
                    double newThreshold = (((int)lastNotionalProfitPct/5)-1)*5 ;
                    if( newThreshold > 10 ) {
                        minProfitThreshold.setThreshold( newThreshold ) ;
                        log.debug( "Setting new min profit threshold as {}", fmtDbl( newThreshold ) ) ;
                    }
                }
                
                boolean minProfitBreached = minProfitIndicator.isSatisfied( index ) ;
                boolean maxLossBreached   = maxLossIndicator.isSatisfied( index ) ;
                
                if( minProfitBreached || maxLossBreached ) {
                    if( minProfitBreached ) {
                        log.info( "Min profit breached." ) ;
                    }
                    else {
                        log.info( "Max loss breached." ) ;
                    }
                    log.debug( "  Last profit % - {}", fmtDbl( lastNotionalProfitPct ) ) ;
                    log.debug( "  Cur profit % - {}", fmtDbl( curNotionalProfitPct ) ) ;
                    processSellTrade( new SellTrade( dayValue.getDate(),
                            dayValue.getBar().getClosePrice().doubleValue(),
                            super.getHoldingQty() ) ) ;
                }
            }
        }
    }
}
