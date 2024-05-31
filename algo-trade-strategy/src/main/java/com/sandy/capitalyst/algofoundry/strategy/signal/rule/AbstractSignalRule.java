package com.sandy.capitalyst.algofoundry.strategy.signal.rule;

import com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries;
import org.ta4j.core.Rule;

public abstract class AbstractSignalRule extends SignalRule {
    
    private Rule rule ;
    
    public AbstractSignalRule( CandleSeries history ) {
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
