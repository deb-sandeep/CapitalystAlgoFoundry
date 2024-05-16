package com.sandy.capitalyst.algofoundry.trigger.rule;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.trigger.TradeRule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.rules.CrossedDownIndicatorRule;

public class EMADownCrossoverRule extends TradeRule {
    
    private EMAIndicator             shortIndicator ;
    private EMAIndicator             longIndicator ;
    private CrossedDownIndicatorRule rule ;
    
    public EMADownCrossoverRule( EquityEODHistory history, int shortWindow, int longWindow ) {
        super( history ) ;
        shortIndicator = history.getEMAIndicator( shortWindow ) ;
        longIndicator  = history.getEMAIndicator( longWindow ) ;
        rule = new CrossedDownIndicatorRule( shortIndicator, longIndicator ) ;
    }
    
    @Override
    public boolean isTriggered( int index ) {
        return rule.isSatisfied( index, null ) ;
    }
}
