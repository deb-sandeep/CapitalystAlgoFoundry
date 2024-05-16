package com.sandy.capitalyst.algofoundry.strategy.rule.atom.ema;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

public class EMAUpCrossoverRule extends AbstractEMARule {
    
    public EMAUpCrossoverRule( EquityEODHistory history, int shortWindow, int longWindow ) {
        super( history, shortWindow, longWindow ) ;
    }
    
    @Override
    protected Rule createRule() {
        return new CrossedUpIndicatorRule( shortIndicator, longIndicator ) ;
    }
}
