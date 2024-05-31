package com.sandy.capitalyst.algofoundry.strategy.signal.rule;

import com.sandy.capitalyst.algofoundry.strategy.eodhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.signal.rule.logic.AndRule;
import com.sandy.capitalyst.algofoundry.strategy.signal.rule.logic.NotRule;
import com.sandy.capitalyst.algofoundry.strategy.signal.rule.logic.OrRule;

public abstract class SignalRule {
    
    protected final EquityEODHistory history ;
    
    protected SignalRule( EquityEODHistory history ) {
        this.history = history ;
    }
    
    public SignalRule and( SignalRule rule ) {
        return new AndRule( this, rule ) ;
    }
    
    public SignalRule or( SignalRule rule ) {
        return new OrRule( this, rule ) ;
    }
    
    public SignalRule not() {
        return new NotRule( this ) ;
    }
    
    public abstract boolean isTriggered( int index ) ;
}
