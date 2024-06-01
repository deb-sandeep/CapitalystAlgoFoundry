package com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.adx;

import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.OverIndicatorRule;

public class ADXStrengthRule extends AbstractADXRule {

    private final int strengthThreshold ;
    
    public ADXStrengthRule( CandleSeries history, int strengthThreshold ) {
        super( history ) ;
        this.strengthThreshold = strengthThreshold ;
    }
    
    @Override
    protected Rule createRule() {
        return new OverIndicatorRule( this.adx, this.strengthThreshold ) ;
    }
}
