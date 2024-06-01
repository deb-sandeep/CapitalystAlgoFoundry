package com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.ema;

import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

public class EMAUpCrossoverRule extends AbstractEMARule {
    
    public EMAUpCrossoverRule( CandleSeries history ) {
        super( history ) ;
    }
    
    public EMAUpCrossoverRule( CandleSeries history, int minWindow, int maxWindow ) {
        super( history, minWindow, maxWindow ) ;
    }
    
    @Override
    protected Rule createRule() {
        return new CrossedUpIndicatorRule( shortIndicator, longIndicator ) ;
    }
}
