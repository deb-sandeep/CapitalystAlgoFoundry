package com.sandy.capitalyst.algofoundry.strategy.rule.atom.adx;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.OverIndicatorRule;

public class ADXStrengthRule extends AbstractADXRule {

    private final int strengthThreshold ;
    
    public ADXStrengthRule( EquityEODHistory history, int strengthThreshold ) {
        super( history ) ;
        this.strengthThreshold = strengthThreshold ;
    }
    
    @Override
    protected Rule createRule() {
        return new OverIndicatorRule( this.adx, this.strengthThreshold ) ;
    }
}
