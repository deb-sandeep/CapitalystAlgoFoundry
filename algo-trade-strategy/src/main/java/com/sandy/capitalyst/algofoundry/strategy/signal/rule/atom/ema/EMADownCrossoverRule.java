package com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.ema;

import com.sandy.capitalyst.algofoundry.strategy.eodhistory.EquityEODHistory;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.CrossedDownIndicatorRule;

public class EMADownCrossoverRule extends AbstractEMARule {
    
    public EMADownCrossoverRule( EquityEODHistory history ) {
        super( history ) ;
    }
    
    public EMADownCrossoverRule( EquityEODHistory history, int minWindow, int maxWindow ) {
        super( history, minWindow, maxWindow ) ;
    }
    
    @Override
    protected Rule createRule() {
        return new CrossedDownIndicatorRule( shortIndicator, longIndicator ) ;
    }
}
