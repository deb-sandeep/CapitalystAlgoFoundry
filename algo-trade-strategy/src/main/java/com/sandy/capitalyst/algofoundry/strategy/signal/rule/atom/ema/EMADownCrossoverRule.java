package com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.ema;

import com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.CrossedDownIndicatorRule;

public class EMADownCrossoverRule extends AbstractEMARule {
    
    public EMADownCrossoverRule( CandleSeries history ) {
        super( history ) ;
    }
    
    public EMADownCrossoverRule( CandleSeries history, int minWindow, int maxWindow ) {
        super( history, minWindow, maxWindow ) ;
    }
    
    @Override
    protected Rule createRule() {
        return new CrossedDownIndicatorRule( shortIndicator, longIndicator ) ;
    }
}
