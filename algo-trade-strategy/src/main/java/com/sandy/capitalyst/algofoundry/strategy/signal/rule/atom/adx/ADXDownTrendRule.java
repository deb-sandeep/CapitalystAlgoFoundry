package com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.adx;

import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.UnderIndicatorRule;

public class ADXDownTrendRule extends AbstractADXRule {

    public ADXDownTrendRule( CandleSeries history ) {
        super( history ) ;
    }
    
    @Override
    protected Rule createRule() {
        return new UnderIndicatorRule( plusDMI, minusDMI ) ;
    }
}
