package com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.ema;

import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.UnderIndicatorRule;

public class EMADownTrendRule extends AbstractEMARule {
    
    public EMADownTrendRule( CandleSeries history ) {
        super( history ) ;
    }
    
    public EMADownTrendRule( CandleSeries history, int minWindow, int maxWindow ) {
        super( history, minWindow, maxWindow ) ;
    }
    
    @Override
    protected Rule createRule() {
        return new UnderIndicatorRule( shortIndicator, longIndicator ) ;
    }
}
