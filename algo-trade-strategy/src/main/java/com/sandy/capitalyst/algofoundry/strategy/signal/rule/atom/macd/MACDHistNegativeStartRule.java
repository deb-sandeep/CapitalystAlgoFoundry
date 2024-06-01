package com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.macd;

import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.CrossedDownIndicatorRule;

public class MACDHistNegativeStartRule extends AbstractMACDRule {
    
    public MACDHistNegativeStartRule( CandleSeries history) {
        super( history );
    }
    
    @Override
    protected Rule createRule() {
        return new CrossedDownIndicatorRule( macdHist, 0.0 ) ;
    }
}
