package com.sandy.capitalyst.algofoundry.trigger.rule.logic;

import com.sandy.capitalyst.algofoundry.trigger.TradeRule;

import java.util.ArrayList;
import java.util.List;

public class AndRule extends LogicRule {
    
    private final List<TradeRule> tradeRules = new ArrayList<>() ;
    
    public AndRule( TradeRule r1, TradeRule r2, TradeRule... rules ) {
        tradeRules.add( r1 ) ;
        tradeRules.add( r2 ) ;
        if( rules != null ) {
            tradeRules.addAll( List.of( rules ) ) ;
        }
    }
    
    @Override
    public boolean isTriggered( int index ) {
        for( TradeRule rule : tradeRules ) {
            if( !rule.isTriggered( index ) ) {
                return false ;
            }
        }
        return true ;
    }
}
