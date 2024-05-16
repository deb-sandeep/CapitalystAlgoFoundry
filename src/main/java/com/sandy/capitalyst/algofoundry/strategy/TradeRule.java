package com.sandy.capitalyst.algofoundry.strategy;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.rule.logic.AndRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.logic.NotRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.logic.OrRule;

public abstract class TradeRule {
    
    protected final EquityEODHistory history ;
    
    protected TradeRule( EquityEODHistory history ) {
        this.history = history ;
    }
    
    public TradeRule and( TradeRule rule ) {
        return new AndRule( this, rule ) ;
    }
    
    public TradeRule or( TradeRule rule ) {
        return new OrRule( this, rule ) ;
    }
    
    public TradeRule not() {
        return new NotRule( this ) ;
    }
    
    public abstract boolean isTriggered( int index ) ;
}
