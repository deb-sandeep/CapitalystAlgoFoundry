package com.sandy.capitalyst.algofoundry.strategy.strategy;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.TradeRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.adx.ADXDownTrendRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.adx.ADXStrengthRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.adx.ADXUpTrendRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.ema.EMADownCrossoverRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.ema.EMAUpCrossoverRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.macd.*;
import com.sandy.capitalyst.algofoundry.strategy.rule.logic.AndRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.logic.OrRule;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.numeric.NumericIndicator;
import org.ta4j.core.num.Num;

public class MyTradeStrategy extends AbstractZonedTradeStrategy {
    
    public static final String NAME = "My Strategy" ;
    
    private final EMAIndicator shortIndicator ;
    private final EMAIndicator longIndicator ;
    private final Indicator<Num> divergenceIndicator ;

    public MyTradeStrategy( EquityEODHistory history ) {
        super( history ) ;
        shortIndicator = history.getEMAIndicator( 5 ) ;
        longIndicator = history.getEMAIndicator( 20 ) ;
        divergenceIndicator = NumericIndicator.of( shortIndicator )
                                              .minus( longIndicator ) ;
    }
    
    @Override
    protected boolean isEntryPreconditionMet( int seriesIndex ) {
        int numDaysIntoActivationPeriod = super.getNumDaysIntoEntryActivationPeriod() ;
        double triggerPointPrice = longIndicator.getValue( seriesIndex-numDaysIntoActivationPeriod ).doubleValue() ;
        double currentDivergence = divergenceIndicator.getValue( seriesIndex ).doubleValue() ;
        
        return Math.abs((currentDivergence/triggerPointPrice)*100) >= 2 ;
    }
    
    @Override
    protected boolean isExitPreconditionMet( int seriesIndex ) {
        int numDaysIntoActivationPeriod = super.getNumDaysIntoExitActivationPeriod() ;
        double triggerPointPrice = longIndicator.getValue( seriesIndex-numDaysIntoActivationPeriod ).doubleValue() ;
        double currentDivergence = divergenceIndicator.getValue( seriesIndex ).doubleValue() ;
        
        return Math.abs((currentDivergence/triggerPointPrice)*100) >= 2 ;
    }
    
    @Override
    protected TradeRule createEntryActivationRule() {
        return new MACDHistPositiveStartRule( history )
                .or( new EMAUpCrossoverRule( history ) );
    }
    
    @Override
    protected TradeRule createExitActivationRule() {
        return new MACDHistNegativeStartRule( history )
                .or( new EMADownCrossoverRule( history ) ) ;
    }
    
    @Override
    protected TradeRule createEntryRule() {
        return new AndRule(
            new ADXUpTrendRule( history ),
            new ADXStrengthRule( history, 25 )
        ) ;
    }
    
    @Override
    protected TradeRule createExitRule() {
        return new AndRule(
            new ADXDownTrendRule( history ),
            new ADXStrengthRule( history, 25 )
        ) ;
    }
}
