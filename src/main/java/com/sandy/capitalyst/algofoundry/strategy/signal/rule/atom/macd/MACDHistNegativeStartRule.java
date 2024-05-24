package com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.macd;

import com.sandy.capitalyst.algofoundry.eodhistory.EquityEODHistory;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.CrossedDownIndicatorRule;

public class MACDHistNegativeStartRule extends AbstractMACDRule {
    
    public MACDHistNegativeStartRule( EquityEODHistory history) {
        super( history );
    }
    
    @Override
    protected Rule createRule() {
        return new CrossedDownIndicatorRule( macdHist, 0.0 ) ;
    }
}
