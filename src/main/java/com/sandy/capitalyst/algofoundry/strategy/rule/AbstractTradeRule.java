package com.sandy.capitalyst.algofoundry.strategy.rule;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.TradeRule;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.rules.CrossedDownIndicatorRule;

public abstract class AbstractTradeRule extends TradeRule {
    
    private Rule  rule ;
    
    public AbstractTradeRule( EquityEODHistory history ) {
        super( history ) ;
    }
    
    protected final Rule getRule() {
        if( rule == null ) {
            rule = createRule() ;
        }
        return rule ;
    }
    
    protected abstract Rule createRule() ;
    
    @Override
    public boolean isTriggered( int index ) {
        return getRule().isSatisfied( index, null ) ;
    }
}
