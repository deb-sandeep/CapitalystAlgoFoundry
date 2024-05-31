package com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.macd;

import com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.OverIndicatorRule;

public class MACDHistPositiveRule extends AbstractMACDRule {
    
    public MACDHistPositiveRule( CandleSeries history ) {
        super( history );
    }
    
    @Override
    protected Rule createRule() {
        return new OverIndicatorRule( macdHist, 0.0 ) ;
    }
}
