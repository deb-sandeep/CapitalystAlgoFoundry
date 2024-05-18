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
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.indicators.numeric.NumericIndicator;
import org.ta4j.core.num.Num;

@Slf4j
public class MyTradeStrategy extends AbstractZonedTradeStrategy {
    
    public static final String NAME = "My Strategy" ;
    
    private final EMAIndicator   ema5;
    private final EMAIndicator   ema20;
    private final Indicator<Num> emaDiff;

    public MyTradeStrategy( EquityEODHistory history ) {
        super( history ) ;
        ema5 = history.getEMAIndicator( 5 ) ;
        ema20 = history.getEMAIndicator( 20 ) ;
        emaDiff = NumericIndicator.of( ema5 ).minus( ema20 ) ;
    }
    
    protected boolean isEntryActivationTriggered( int seriesIndex ) {
        boolean superResult = super.isEntryActivationTriggered( seriesIndex ) ;
        boolean emaDiffJump = hasValuePctChanged( emaDiff, ema20, seriesIndex, 3, 5 ) ;
        boolean ema20Jump   = hasValuePctChanged( ema20, seriesIndex, 3, 5 ) ;
        return superResult || emaDiffJump || ema20Jump ;
    }
    
    protected boolean isExitActivationTriggered( int seriesIndex ) {
        boolean superResult = super.isExitActivationTriggered( seriesIndex ) ;
        boolean emaJump = hasValuePctChanged( emaDiff, ema20, seriesIndex, 3, -5 ) ;
        boolean ema20Jump   = hasValuePctChanged( ema20, seriesIndex, 3, -5 ) ;
        return superResult || emaJump  || ema20Jump ;
    }
    
    @Override
    protected boolean isEntryPreconditionMet( int seriesIndex ) {
        int activationAge = super.getNumDaysIntoEntryActivationPeriod() ;
        return hasValuePctChanged( emaDiff, seriesIndex, activationAge, 3 ) ;
    }
    
    @Override
    protected boolean isExitPreconditionMet( int seriesIndex ) {
        int activationAge = super.getNumDaysIntoExitActivationPeriod() ;
        return hasValuePctChanged( emaDiff, seriesIndex, activationAge, -3 ) ;
    }
    
    private boolean hasValuePctChanged( Indicator<Num> series,
                                        int index, int gapDays, double pct ) {
        return hasValuePctChanged( series, null, index, gapDays, pct ) ;
    }

    private boolean hasValuePctChanged( Indicator<Num> series, Indicator<Num> ref,
                                        int index, int gapDays, double pct ) {
        
        Indicator<Num>   prev = new PreviousValueIndicator( series, gapDays ) ;
        CombineIndicator diff = CombineIndicator.minus( series, prev ) ;
        
        double change = diff.getValue( index ).doubleValue() ;
        double refVal = 0 ;
        
        if( ref != null ) {
            refVal = ref.getValue( index-gapDays ).doubleValue() ;
        }
        else {
            refVal = prev.getValue( index ).doubleValue() ;
        }

        double chgPct = ( change/refVal )*100 ;
        
        return pct > 0 ? chgPct >= pct : chgPct <= pct ;
    }
    
    @Override
    protected TradeRule createEntryActivationRule() {
        return new MACDHistPositiveStartRule( history )
                .or( new EMAUpCrossoverRule( history ) ) ;
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
