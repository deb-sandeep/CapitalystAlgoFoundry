package com.sandy.capitalyst.algofoundry.strategy.rule.atom.ema;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

public class EMADownTrendRule extends AbstractEMARule {
    
    public EMADownTrendRule( EquityEODHistory history ) {
        super( history ) ;
    }
    
    public EMADownTrendRule( EquityEODHistory history, int minWindow, int maxWindow ) {
        super( history, minWindow, maxWindow ) ;
    }
    
    @Override
    protected Rule createRule() {
        return new UnderIndicatorRule( shortIndicator, longIndicator ) ;
    }
}
