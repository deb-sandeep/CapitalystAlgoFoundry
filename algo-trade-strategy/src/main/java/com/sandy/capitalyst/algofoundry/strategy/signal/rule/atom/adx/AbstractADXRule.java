package com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.adx;

import com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries;
import com.sandy.capitalyst.algofoundry.strategy.signal.rule.AbstractSignalRule;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.numeric.NumericIndicator;
import org.ta4j.core.num.Num;

public abstract class AbstractADXRule extends AbstractSignalRule {

    protected final Indicator<Num> adx ;
    protected final Indicator<Num> plusDMI ;
    protected final Indicator<Num> minusDMI ;
    protected final Indicator<Num> dmiDiff ;
    
    public AbstractADXRule( CandleSeries history ) {
        super( history ) ;
        this.adx      = history.ind( CandleSeries.IndicatorName.ADX ) ;
        this.plusDMI  = history.ind( CandleSeries.IndicatorName.ADX_PLUS_DMI ) ;
        this.minusDMI = history.ind( CandleSeries.IndicatorName.ADX_MINUS_DMI ) ;
        this.dmiDiff  = NumericIndicator.of( plusDMI ).minus( minusDMI ).abs() ;
    }
}
