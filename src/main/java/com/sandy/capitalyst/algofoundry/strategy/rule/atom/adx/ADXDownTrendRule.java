package com.sandy.capitalyst.algofoundry.strategy.rule.atom.adx;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

public class ADXDownTrendRule extends AbstractADXRule {

    public ADXDownTrendRule( EquityEODHistory history ) {
        super( history ) ;
    }
    
    @Override
    protected Rule createRule() {
        return new UnderIndicatorRule( plusDMI, minusDMI ) ;
    }
}
