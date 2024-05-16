package com.sandy.capitalyst.algofoundry.strategy.rule.atom.macd;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.OverIndicatorRule;

public class MACDHistPositiveRule extends AbstractMACDRule {
    
    public MACDHistPositiveRule( EquityEODHistory history ) {
        super( history );
    }
    
    @Override
    protected Rule createRule() {
        return new OverIndicatorRule( macdHist, 0.0 ) ;
    }
}
