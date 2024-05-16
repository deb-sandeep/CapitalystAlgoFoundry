package com.sandy.capitalyst.algofoundry.trigger.rule.atom;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.trigger.TradeRule;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.OverIndicatorRule;

public class ADXStrengthRule extends TradeRule {

    private final Indicator<Num> adx ;
    private final Indicator<Num> plusDMI ;
    private final Indicator<Num> minusDMI ;
    
    private final int strengthThreshold ;
    
    private final OverIndicatorRule strengthRule ;
    
    public ADXStrengthRule( EquityEODHistory history, int strengthThreshold ) {
        super( history ) ;
        
        this.strengthThreshold = strengthThreshold ;
        this.adx      = history.ind( EquityEODHistory.IndicatorName.ADX ) ;
        this.plusDMI  = history.ind( EquityEODHistory.IndicatorName.ADX_PLUS_DMI ) ;
        this.minusDMI = history.ind( EquityEODHistory.IndicatorName.ADX_MINUS_DMI ) ;
        
        this.strengthRule = new OverIndicatorRule( this.adx, this.strengthThreshold ) ;
    }
    
    @Override
    public boolean isTriggered( int index ) {
        return strengthRule.isSatisfied( index, null ) ;
    }
}
