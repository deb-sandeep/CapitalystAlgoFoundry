package com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.ema;

import com.sandy.capitalyst.algofoundry.strategy.eodhistory.EquityEODHistory;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.OverIndicatorRule;

public class EMAUpTrendRule extends AbstractEMARule {
    
    public EMAUpTrendRule( EquityEODHistory history ) {
        super( history ) ;
    }
    
    public EMAUpTrendRule( EquityEODHistory history, int minWindow, int maxWindow ) {
        super( history, minWindow, maxWindow ) ;
    }
    
    @Override
    protected Rule createRule() {
        return new OverIndicatorRule( shortIndicator, longIndicator ) ;
    }
}
