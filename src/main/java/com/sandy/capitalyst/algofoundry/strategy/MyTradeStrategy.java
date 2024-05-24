package com.sandy.capitalyst.algofoundry.strategy;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.adx.ADXDownTrendRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.adx.ADXStrengthRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.adx.ADXUpTrendRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.ema.EMADownCrossoverRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.ema.EMAUpCrossoverRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.macd.MACDHistNegativeStartRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.macd.MACDHistPositiveStartRule;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.Bar;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.numeric.NumericIndicator;
import org.ta4j.core.num.Num;

import java.util.Date;

import static com.sandy.capitalyst.algofoundry.core.util.StringUtil.*;

@Slf4j
public class MyTradeStrategy extends AbstractZonedTradeStrategy {
    
    public static final String NAME = "My Strategy" ;
    
    private static final int    ACTIVATION_TEST_GAP     = 3 ;
    private static final double ACTIVATION_EMA_DIFF_THR = 2 ;
    private static final double ACTIVATION_EMA_THR      = 1 ;
    
    private static final double SIGNAL_EMA_THR = 1 ;
    
    private final EMAIndicator   ema5;
    private final EMAIndicator   ema20;
    private final Indicator<Num> emaDiff;
    
    private final boolean logRootCause = true ;
    
    private MACDHistPositiveStartRule macdPosStart;
    private MACDHistNegativeStartRule macdNegStart;
    private EMAUpCrossoverRule        emaUpCrossover ;
    private EMADownCrossoverRule      emaDownCrossover ;
    private ADXUpTrendRule            adxUpTrend ;
    private ADXDownTrendRule          adxDownTrend ;
    private ADXStrengthRule           adxStrength ;

    public MyTradeStrategy( EquityEODHistory history ) {
        
        super( history ) ;
        
        ema5 = history.getEMAIndicator( 5 );
        ema20 = history.getEMAIndicator( 20 ) ;
        emaDiff = NumericIndicator.of( ema5 ).minus( ema20 ) ;
        
        createRules() ;
    }
    
    private void createRules() {
        macdPosStart = new MACDHistPositiveStartRule( history ) ;
        macdNegStart = new MACDHistNegativeStartRule( history ) ;
        
        emaUpCrossover = new EMAUpCrossoverRule( history ) ;
        emaDownCrossover = new EMADownCrossoverRule( history ) ;
        
        adxUpTrend = new ADXUpTrendRule( history ) ;
        adxDownTrend = new ADXDownTrendRule( history ) ;
        
        adxStrength = new ADXStrengthRule( history, 25 ) ;
    }
    
    @Override
    protected boolean isEntryZoneTriggered( int index ) {
        
        boolean macdPosStartRes = macdPosStart.isTriggered( index ) ;
        boolean emaUpCrossoverRes = emaUpCrossover.isTriggered( index ) ;
        boolean emaDiffJump = hasValuePctChanged( "EMA diff",
                                                    emaDiff, ema20, index,
                                                    ACTIVATION_TEST_GAP,
                                                    ACTIVATION_EMA_DIFF_THR ) ;
        boolean ema20Jump   = hasValuePctChanged( "EMA jump",
                                                    ema20, ema20, index,
                                                    ACTIVATION_TEST_GAP,
                                                    ACTIVATION_EMA_THR ) ;
        
        boolean result = macdPosStartRes || emaUpCrossoverRes || emaDiffJump || ema20Jump ;

        info1( bs( result ) + " Entry zone trigger check" );
        if( logRootCause ) {
            info2( bs( macdPosStartRes   ) + " MACD positive start" ) ;
            info2( bs( emaUpCrossoverRes ) + " EMA up crossover" ) ;
            info2( bs( emaDiffJump       ) + " EMA diff > " + ACTIVATION_EMA_DIFF_THR + "% in " + ACTIVATION_TEST_GAP + " days" ) ;
            info2( bs( ema20Jump         ) + " EMA 20 diff > " + ACTIVATION_EMA_THR + "% in " + ACTIVATION_TEST_GAP + " days" ) ;
        }
        
        return result ;
    }
    
    @Override
    protected boolean isExitZoneTriggered( int index ) {
        
        boolean macdNegStartRes = macdNegStart.isTriggered( index ) ;
        boolean emaDownCrossoverRes = emaDownCrossover.isTriggered( index ) ;
        boolean emaDiffJump = hasValuePctChanged( "EMA diff",
                                                    emaDiff, ema20, index,
                                                    ACTIVATION_TEST_GAP,
                                                    -ACTIVATION_EMA_DIFF_THR ) ;
        boolean ema20Jump   = hasValuePctChanged( "EMA jump",
                                                    ema20, ema20, index,
                                                    ACTIVATION_TEST_GAP,
                                                    -ACTIVATION_EMA_THR ) ;
        
        boolean result = macdNegStartRes || emaDownCrossoverRes || emaDiffJump || ema20Jump ;

        info1( bs( result ) + " Exit zone trigger check" );
        if( logRootCause ) {
            info2( bs( macdNegStartRes     ) + " MACD negative start" ) ;
            info2( bs( emaDownCrossoverRes ) + " EMA down crossover" ) ;
            info2( bs( emaDiffJump         ) + " EMA diff < -" + ACTIVATION_EMA_DIFF_THR + "% in " + ACTIVATION_TEST_GAP + " days" ) ;
            info2( bs( ema20Jump           ) + " EMA 20 diff < -" + ACTIVATION_EMA_THR + "% in " + ACTIVATION_TEST_GAP + " days" ) ;
        }

        return result ;
    }
    
    @Override
    protected boolean isEntryConditionMet( int index ) {
        
        int activationAge      = super.getNumDaysIntoEntryZone() ;
        double pctThreshold    = SIGNAL_EMA_THR + (activationAge-1)*0.125 ;
        boolean adxUpTrendRes  = adxUpTrend.isTriggered( index ) ;
        boolean adxStrengthRes = adxStrength.isTriggered( index ) ;
        boolean ema20Jump      = hasValuePctChanged( "EMA jump",
                                                    ema5, ema5, index,
                                                    activationAge,
                                                    pctThreshold ) ;
        
        boolean result = ema20Jump && adxUpTrendRes && adxStrengthRes ;

        info1( bs( result ) + " Entry condition check" );
        if( logRootCause ) {
            info2( bs( adxUpTrendRes  ) + " ADX up trend" ) ;
            info2( bs( adxStrengthRes ) + " ADX strength > 25" ) ;
            info2( bs( ema20Jump      ) + " EMA jump > " +
                    pctThreshold + "% in " + activationAge + " days" ) ;
        }
        
        return result ;
    }
    
    @Override
    protected boolean isExitConditionMet( int index ) {
        
        int activationAge = super.getNumDaysIntoExitZone() ;
        double pctThreshold = -SIGNAL_EMA_THR - (activationAge-1)*0.125 ;
        boolean adxDownTrendRes = adxDownTrend.isTriggered( index ) ;
        boolean adxStrengthRes  = adxStrength.isTriggered( index ) ;
        boolean ema20Jump       = hasValuePctChanged( "EMA jump",
                                                        ema5, ema5, index,
                                                        activationAge,
                                                        pctThreshold ) ;
        
        boolean result = ema20Jump && adxDownTrendRes && adxStrengthRes ;

        info1( bs( result ) + " Exit condition check" );
        if( logRootCause ) {
            info2( bs( adxDownTrendRes ) + " ADX down trend" ) ;
            info2( bs( adxStrengthRes  ) + " ADX strength > 25" ) ;
            info2( bs( ema20Jump       ) + " EMA jump < -" +
                    pctThreshold + "% in " + activationAge + " days" ) ;
        }
        
        return result ;
    }
    
    private boolean hasValuePctChanged( String label, Indicator<Num> series, Indicator<Num> ref,
                                        int index, int gapDays, double pct ) {
        
        Bar currentBar = history.getBarSeries().getBar( index ) ;
        Bar prevBar = history.getBarSeries().getBar( index - gapDays ) ;
        
        double refVal ;
        double change = series.getValue( index ).doubleValue() -
                        series.getValue( index-gapDays ).doubleValue() ;
        
        if( ref != null ) {
            refVal = ref.getValue( index-gapDays ).doubleValue() ;
        }
        else {
            refVal = series.getValue( index-gapDays ).doubleValue() ;
        }
        
        double chgPct = ( change/refVal )*100 ;
        log.debug( "Jump check - {}", label ) ;
        log.debug( "Current date = {}", fmtDate( Date.from( currentBar.getEndTime().toInstant() ) ) ) ;
        log.debug( "Past    date = {}", fmtDate( Date.from( prevBar.getEndTime().toInstant() ) ) ) ;
        log.debug( "  #Days = {}, Change = {}, ref = {}, changePct = {}%, threshold={}%",
                   gapDays, fmtDbl( change ), fmtDbl( refVal ), fmtDbl( chgPct ), pct ) ;
        
        return pct > 0 ? chgPct >= pct : chgPct <= pct ;
    }
}
