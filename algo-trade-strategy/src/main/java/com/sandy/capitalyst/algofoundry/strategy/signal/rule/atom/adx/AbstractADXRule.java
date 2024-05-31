package com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.adx;

import com.sandy.capitalyst.algofoundry.strategy.eodhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.signal.rule.AbstractSignalRule;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.numeric.NumericIndicator;
import org.ta4j.core.num.Num;

public abstract class AbstractADXRule extends AbstractSignalRule {

    protected final Indicator<Num> adx ;
    protected final Indicator<Num> plusDMI ;
    protected final Indicator<Num> minusDMI ;
    protected final Indicator<Num> dmiDiff ;
    
    public AbstractADXRule( EquityEODHistory history ) {
        super( history ) ;
        this.adx      = history.ind( EquityEODHistory.IndicatorName.ADX ) ;
        this.plusDMI  = history.ind( EquityEODHistory.IndicatorName.ADX_PLUS_DMI ) ;
        this.minusDMI = history.ind( EquityEODHistory.IndicatorName.ADX_MINUS_DMI ) ;
        this.dmiDiff  = NumericIndicator.of( plusDMI ).minus( minusDMI ).abs() ;
    }
}