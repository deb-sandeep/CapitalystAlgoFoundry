package com.sandy.capitalyst.algofoundry.strategy.rule.atom.ema;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.rule.AbstractTradeRule;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.numeric.NumericIndicator;
import org.ta4j.core.num.Num;

public abstract class AbstractEMARule extends AbstractTradeRule {
    
    protected EMAIndicator shortIndicator ;
    protected EMAIndicator longIndicator ;
    protected Indicator<Num> divergenceIndicator ;
    
    public AbstractEMARule( EquityEODHistory history ) {
        this( history, 5, 20 ) ;
    }

    public AbstractEMARule( EquityEODHistory history, int minWindow, int maxWindow ) {
        super( history ) ;
        shortIndicator = history.getEMAIndicator( minWindow ) ;
        longIndicator  = history.getEMAIndicator( maxWindow ) ;
        divergenceIndicator = NumericIndicator.of( shortIndicator ).minus( longIndicator ) ;
    }
}
