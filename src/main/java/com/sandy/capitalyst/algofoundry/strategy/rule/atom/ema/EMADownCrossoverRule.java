package com.sandy.capitalyst.algofoundry.strategy.rule.atom.ema;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.CrossedDownIndicatorRule;

public class EMADownCrossoverRule extends AbstractEMARule {
    
    public EMADownCrossoverRule( EquityEODHistory history, int shortWindow, int longWindow ) {
        super( history, shortWindow, longWindow ) ;
    }
    
    @Override
    protected Rule createRule() {
        return new CrossedDownIndicatorRule( shortIndicator, longIndicator ) ;
    }
}
