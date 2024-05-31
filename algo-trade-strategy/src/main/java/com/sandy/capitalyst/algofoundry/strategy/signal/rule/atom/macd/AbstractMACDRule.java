package com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.macd;

import com.sandy.capitalyst.algofoundry.strategy.eodhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.signal.rule.AbstractSignalRule;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.numeric.NumericIndicator;
import org.ta4j.core.num.Num;

@Slf4j
public abstract class AbstractMACDRule extends AbstractSignalRule {
    
    protected Indicator<Num> macd ;
    protected Indicator<Num> macdSignal ;
    protected Indicator<Num> macdHist ;
    
    public AbstractMACDRule( EquityEODHistory history ) {
        super( history ) ;
        macd = history.ind( EquityEODHistory.IndicatorName.MACD ) ;
        macdSignal = history.ind( EquityEODHistory.IndicatorName.MACD_SIGNAL ) ;
        macdHist = NumericIndicator.of( macd ).minus( macdSignal ) ;
    }
}
