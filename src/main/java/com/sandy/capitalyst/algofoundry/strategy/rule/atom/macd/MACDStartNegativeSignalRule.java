package com.sandy.capitalyst.algofoundry.strategy.rule.atom.macd;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

public class MACDStartNegativeSignalRule extends AbstractMACDRule {
    
    public MACDStartNegativeSignalRule( EquityEODHistory history ) {
        super( history );
    }
    
    @Override
    protected Rule createRule() {
        return new CrossedDownIndicatorRule( macdHistNorm, 0.0 ) ;
    }
}
