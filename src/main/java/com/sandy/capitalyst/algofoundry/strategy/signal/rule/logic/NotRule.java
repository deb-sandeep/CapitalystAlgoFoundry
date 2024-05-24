package com.sandy.capitalyst.algofoundry.strategy.signal.rule.logic;

import com.sandy.capitalyst.algofoundry.strategy.signal.rule.SignalRule;

public class NotRule extends LogicRule {
    
    private final SignalRule tradeRule ;
    
    public NotRule( SignalRule rule ) {
        this.tradeRule = rule ;
    }
    
    @Override
    public boolean isTriggered( int index ) {
        return !tradeRule.isTriggered( index ) ;
    }
}
