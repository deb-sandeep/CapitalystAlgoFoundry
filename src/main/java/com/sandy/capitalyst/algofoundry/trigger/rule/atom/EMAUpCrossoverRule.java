package com.sandy.capitalyst.algofoundry.trigger.rule.atom;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.trigger.TradeRule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

public class EMAUpCrossoverRule extends TradeRule {
    
    private EMAIndicator shortIndicator ;
    private EMAIndicator longIndicator ;
    private CrossedUpIndicatorRule rule ;
    
    public EMAUpCrossoverRule( EquityEODHistory history, int shortWindow, int longWindow ) {
        super( history ) ;
        shortIndicator = history.getEMAIndicator( shortWindow ) ;
        longIndicator  = history.getEMAIndicator( longWindow ) ;
        rule = new CrossedUpIndicatorRule( shortIndicator, longIndicator ) ;
    }
    
    @Override
    public boolean isTriggered( int index ) {
        return rule.isSatisfied( index, null ) ;
    }
}
