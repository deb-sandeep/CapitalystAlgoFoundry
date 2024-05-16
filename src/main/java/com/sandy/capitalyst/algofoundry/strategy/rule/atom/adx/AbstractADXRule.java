package com.sandy.capitalyst.algofoundry.strategy.rule.atom.adx;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.TradeRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.AbstractTradeRule;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.OverIndicatorRule;

public abstract class AbstractADXRule extends AbstractTradeRule {

    protected final Indicator<Num> adx ;
    protected final Indicator<Num> plusDMI ;
    protected final Indicator<Num> minusDMI ;
    
    public AbstractADXRule( EquityEODHistory history ) {
        super( history ) ;
        this.adx      = history.ind( EquityEODHistory.IndicatorName.ADX ) ;
        this.plusDMI  = history.ind( EquityEODHistory.IndicatorName.ADX_PLUS_DMI ) ;
        this.minusDMI = history.ind( EquityEODHistory.IndicatorName.ADX_MINUS_DMI ) ;
    }
}
