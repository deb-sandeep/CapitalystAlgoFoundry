package com.sandy.capitalyst.algofoundry.trigger;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;

public abstract class TradeRule {
    
    protected final EquityEODHistory history ;
    
    protected TradeRule( EquityEODHistory history ) {
        this.history = history ;
    }
    
    public abstract boolean isTriggered( int index ) ;
}
