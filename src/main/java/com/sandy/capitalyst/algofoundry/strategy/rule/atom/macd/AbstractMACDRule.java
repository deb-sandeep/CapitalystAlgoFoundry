package com.sandy.capitalyst.algofoundry.strategy.rule.atom.macd;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.rule.AbstractTradeRule;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.numeric.NumericIndicator;
import org.ta4j.core.num.Num;

import static com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory.IndicatorName.MACD;
import static com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory.IndicatorName.MACD_SIGNAL;

@Slf4j
public abstract class AbstractMACDRule extends AbstractTradeRule {
    
    protected Indicator<Num> macd ;
    protected Indicator<Num> macdSignal ;
    protected Indicator<Num> macdHist ;
    
    public AbstractMACDRule( EquityEODHistory history ) {
        super( history ) ;
        macd = history.ind( MACD ) ;
        macdSignal = history.ind( MACD_SIGNAL ) ;
        macdHist = NumericIndicator.of( macd ).minus( macdSignal ) ;
    }
}
