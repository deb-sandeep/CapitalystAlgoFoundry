package com.sandy.capitalyst.algofoundry.strategy.rule.atom.ema;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.TradeRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.AbstractTradeRule;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.rules.CrossedDownIndicatorRule;

public abstract class AbstractEMARule extends AbstractTradeRule {
    
    protected EMAIndicator shortIndicator ;
    protected EMAIndicator longIndicator ;
    
    public AbstractEMARule( EquityEODHistory history, int shortWindow, int longWindow ) {
        super( history ) ;
        shortIndicator = history.getEMAIndicator( shortWindow ) ;
        longIndicator  = history.getEMAIndicator( longWindow ) ;
    }
}
