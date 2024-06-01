package com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.adx;

import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.OverIndicatorRule;

public class ADXUpTrendRule extends AbstractADXRule {

    public ADXUpTrendRule( CandleSeries history ) {
        super( history ) ;
    }
    
    @Override
    protected Rule createRule() {
        return new OverIndicatorRule( plusDMI, minusDMI ) ;
    }
}
