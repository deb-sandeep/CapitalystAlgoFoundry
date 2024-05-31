package com.sandy.capitalyst.algofoundry.strategy.signal.rule.logic;

import com.sandy.capitalyst.algofoundry.strategy.signal.rule.SignalRule;

import java.util.ArrayList;
import java.util.List;

public class AndRule extends LogicRule {
    
    private final List<SignalRule> tradeRules = new ArrayList<>() ;
    
    public AndRule( SignalRule r1, SignalRule r2, SignalRule... rules ) {
        tradeRules.add( r1 ) ;
        tradeRules.add( r2 ) ;
        if( rules != null ) {
            tradeRules.addAll( List.of( rules ) ) ;
        }
    }
    
    @Override
    public boolean isTriggered( int index ) {
        for( SignalRule rule : tradeRules ) {
            if( !rule.isTriggered( index ) ) {
                return false ;
            }
        }
        return true ;
    }
}
