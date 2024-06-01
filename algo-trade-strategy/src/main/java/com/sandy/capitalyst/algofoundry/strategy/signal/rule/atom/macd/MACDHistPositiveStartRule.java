package com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.macd;

import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

public class MACDHistPositiveStartRule extends AbstractMACDRule {
    
    public MACDHistPositiveStartRule( CandleSeries history ) {
        super( history );
    }
    
    @Override
    protected Rule createRule() {
        return new CrossedUpIndicatorRule( macdHist, 0.0 ) ;
    }
}
