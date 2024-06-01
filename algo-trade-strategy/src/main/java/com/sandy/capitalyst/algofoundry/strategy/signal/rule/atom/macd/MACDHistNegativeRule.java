package com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.macd;

import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.UnderIndicatorRule;

public class MACDHistNegativeRule extends AbstractMACDRule {
    
    public MACDHistNegativeRule( CandleSeries history ) {
        super( history );
    }
    
    @Override
    protected Rule createRule() {
        return new UnderIndicatorRule( macdHist, 0.0 ) ;
    }
}
