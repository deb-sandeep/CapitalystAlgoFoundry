package com.sandy.capitalyst.algofoundry.strategy.strategy;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.AbstractTradeStrategy;
import com.sandy.capitalyst.algofoundry.strategy.TradeSignal;
import org.ta4j.core.Bar;

import java.util.Date;

public abstract class TradeStrategy extends AbstractTradeStrategy {
    
    protected TradeStrategy( EquityEODHistory history ) {
        super( history ) ;
    }
    
    public TradeSignal executeStrategy( int seriesIndex, Date date, Bar bar ) {
        
        TradeSignal trade = null ;
        double closingPrice = bar.getClosePrice().doubleValue() ;
        
        if( getEntryRule() != null &&
            getEntryRule().isTriggered( seriesIndex ) ) {
            
            trade = new TradeSignal( TradeSignal.Type.ENTRY, date,
                                     history.getSymbol(), closingPrice ) ;
        }
        else if( getExitRule() != null &&
                 getEntryRule().isTriggered( seriesIndex ) ) {
            
            trade = new TradeSignal( TradeSignal.Type.EXIT, date,
                                     history.getSymbol(), closingPrice ) ;
        }
        return trade ;
    }
}
