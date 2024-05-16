package com.sandy.capitalyst.algofoundry.strategy.rule.atom.macd;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

public class MACDStartPositiveSignalRule extends AbstractMACDRule {
    
    public MACDStartPositiveSignalRule( EquityEODHistory history ) {
        super( history );
    }
    
    @Override
    protected Rule createRule() {
        return new CrossedUpIndicatorRule( macdHistNorm, 0.0 ) ;
    }
}
