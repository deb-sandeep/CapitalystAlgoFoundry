package com.sandy.capitalyst.algofoundry.strategy.rule.logic;

import com.sandy.capitalyst.algofoundry.strategy.TradeRule;

public class NotRule extends LogicRule {
    
    private final TradeRule tradeRule ;
    
    public NotRule( TradeRule rule ) {
        this.tradeRule = rule ;
    }
    
    @Override
    public boolean isTriggered( int index ) {
        return !tradeRule.isTriggered( index ) ;
    }
}
