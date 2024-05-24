package com.sandy.capitalyst.algofoundry.strategy.trade;

import com.sandy.capitalyst.algofoundry.core.indicator.ConstantSeries;
import com.sandy.capitalyst.algofoundry.core.indicator.CrossDownIndicator;
import com.sandy.capitalyst.algofoundry.eodhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.eodhistory.dayvalue.OHLCVDayValue;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.TradeSignalEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultTradeBook extends TradeBook {
    
    private final CrossDownIndicator maxLossIndicator ;
    private final CrossDownIndicator minProfitIndicator ;
    private final ConstantSeries minProfitThreshold ;
    private final ConstantSeries maxLossThreshold ;
    
    private int buyCooloffDaysLeft = 0 ;
    
    public DefaultTradeBook() {
        
        minProfitThreshold = ConstantSeries.constantSeries( 10 ) ;
        maxLossThreshold   = ConstantSeries.constantSeries( -10 ) ;
        
        maxLossIndicator = new CrossDownIndicator( notionalProfitPctSeries, maxLossThreshold ) ;
        minProfitIndicator = new CrossDownIndicator( notionalProfitPctSeries, minProfitThreshold ) ;
    }
    
    @Override
    protected BuyTrade handleBuySignal( TradeSignalEvent te ) {
        
        if( buyCooloffDaysLeft <= 0 ) {
            double investmentQuantum = Math.min( 5*ema60Price, 25000 ) ;
            int quantity = (int)(investmentQuantum/te.getClosingPrice()) ;
            if( quantity > 0 ) {
                log.debug( "Buying {} stocks", quantity ) ;
                buyCooloffDaysLeft = 5 ;
                return new BuyTrade( te.getDate(), te.getClosingPrice(), quantity ) ;
            }
        }
        else {
            log.debug( "In buy cool off period. {} days left", buyCooloffDaysLeft );
        }
        return null ;
    }
    
    @Override
    protected SellTrade handleSellSignal( TradeSignalEvent te ) {
        return new SellTrade( te.getDate(), te.getClosingPrice(), super.getHoldingQty() ) ;
    }
    
    @Override
    public void handleDayValue( AbstractDayValue dayValue ) {
        
        super.handleDayValue( dayValue ) ;
        
        if( dayValue instanceof OHLCVDayValue ohlcv ) {
            if( buyCooloffDaysLeft > 0 ) { buyCooloffDaysLeft-- ; }
            int index = notionalProfitPctSeries.getSize()-1 ;
            
            if( index > 0 ) {
                
                double lastNotionalProfitPct = notionalProfitPctSeries.getValue( index-1 ) ;
                double curNotionalProfitPct  = notionalProfitPctSeries.getValue( index ) ;
                double newThresholdValue = 10 ;
                
                if( lastNotionalProfitPct > 30 ) {
                    newThresholdValue = 25 ;
                }
                else if( lastNotionalProfitPct > 25 ) {
                    newThresholdValue = 20 ;
                }
                else if( lastNotionalProfitPct > 20 ) {
                    newThresholdValue = 15 ;
                }
                else {
                    newThresholdValue = 10 ;
                }
                
                minProfitThreshold.setThreshold( newThresholdValue ) ;
                
                boolean minProfitBreached = minProfitIndicator.isSatisfied( index ) ;
                boolean maxLossBreached   = maxLossIndicator.isSatisfied( index ) ;
                
                if( minProfitBreached || maxLossBreached ) {
                    
                    if( minProfitBreached ) {
                        log.info( "Min profit breached." ) ;
                        log.debug( "  Last profit % - {}", lastNotionalProfitPct ) ;
                        log.debug( "  Cur profit % - {}", curNotionalProfitPct ) ;
                    }
                    else {
                        log.info( "Max loss breached." ) ;
                    }
                    processSellTrade( new SellTrade( dayValue.getDate(),
                            dayValue.getBar().getClosePrice().doubleValue(),
                            super.getHoldingQty() ) ) ;
                    
                    super.updateAttributes( true ) ;
                }
            }
        }
    }
}
