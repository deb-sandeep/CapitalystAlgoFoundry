package com.sandy.capitalyst.algofoundry.strategy.signal;

import com.sandy.capitalyst.algofoundry.strategy.eodhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.adx.ADXDownTrendRule;
import com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.adx.ADXUpTrendRule;
import com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.ema.EMADownCrossoverRule;
import com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.ema.EMAUpCrossoverRule;
import com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.macd.MACDHistNegativeStartRule;
import com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.macd.MACDHistPositiveStartRule;
import com.sandy.capitalyst.algofoundry.strategy.signal.rule.atom.adx.ADXStrengthRule;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.num.Num;

import java.util.Objects;

import static com.sandy.capitalyst.algofoundry.strategy.util.StringUtil.* ;

@Slf4j
public class MySignalStrategy extends AbstractZonedSignalStrategy {
    
    public static final String NAME = "My Strategy" ;
    
    private static final int    ACTIVATION_TEST_GAP = 3 ;
    
    private static final double BUY_ACTIVATION_EMA_THR  = 1 ;
    private static final double SELL_ACTIVATION_EMA_THR = -1 ;
    
    private static final double BUY_SIGNAL_EMA_THR  = 1 ;
    private static final double SELL_SIGNAL_EMA_THR = -1 ;
    
    private final Indicator<Num> cp;
    private final EMAIndicator   ema5;
    private final EMAIndicator   ema20;
    
    private final boolean logRootCause = true ;
    
    private MACDHistPositiveStartRule macdPositiveStart;
    private MACDHistNegativeStartRule macdNegStart;
    private EMAUpCrossoverRule        emaUpCrossover ;
    private EMADownCrossoverRule      emaDownCrossover ;
    private ADXUpTrendRule            adxUpTrend ;
    private ADXDownTrendRule          adxDownTrend ;
    private ADXStrengthRule           adxStrength ;

    public MySignalStrategy( EquityEODHistory history ) {
        
        super( history ) ;
        
        cp = history.ind( EquityEODHistory.IndicatorName.CLOSING_PRICE ) ;
        ema5 = history.getEMAIndicator( 5 );
        ema20 = history.getEMAIndicator( 20 ) ;
        
        createRules() ;
    }
    
    private void createRules() {
        macdPositiveStart = new MACDHistPositiveStartRule( history ) ;
        macdNegStart = new MACDHistNegativeStartRule( history ) ;
        
        emaUpCrossover = new EMAUpCrossoverRule( history ) ;
        emaDownCrossover = new EMADownCrossoverRule( history ) ;
        
        adxUpTrend = new ADXUpTrendRule( history ) ;
        adxDownTrend = new ADXDownTrendRule( history ) ;
        
        adxStrength = new ADXStrengthRule( history, 25 ) ;
    }
    
    @Override
    protected boolean isBuyZoneActivated( int index ) {
        
        boolean didMACDPosZoneStart = macdPositiveStart.isTriggered( index ) ;
        boolean emaUpCrossoverRes   = emaUpCrossover.isTriggered( index ) ;
        boolean ema20Jump           = hasValuePctChanged( "EMA jump",
                                                    ema20, ema20, index,
                                                    ACTIVATION_TEST_GAP,
                                                    BUY_ACTIVATION_EMA_THR ) ;
        
        boolean result = didMACDPosZoneStart || emaUpCrossoverRes || ema20Jump ;

        info1( bs( result ) + " Buy zone trigger check" );
        if( logRootCause ) {
            info2( bs( didMACDPosZoneStart ) + " MACD positive start" ) ;
            info2( bs( emaUpCrossoverRes   ) + " EMA up crossover" ) ;
            info2( bs( ema20Jump           ) + " EMA 20 diff > " + BUY_ACTIVATION_EMA_THR + "% in " + ACTIVATION_TEST_GAP + " days" ) ;
        }
        
        return result ;
    }
    
    @Override
    protected boolean isSellZoneActivated( int index ) {
        
        boolean didMACDNegZoneStart = macdNegStart.isTriggered( index ) ;
        boolean emaDownCrossoverRes = emaDownCrossover.isTriggered( index ) ;
        boolean ema20Jump           = hasValuePctChanged( "EMA jump",
                                                    ema20, ema20, index,
                                                    ACTIVATION_TEST_GAP,
                                                    SELL_ACTIVATION_EMA_THR ) ;
        
        boolean result = didMACDNegZoneStart || emaDownCrossoverRes || ema20Jump ;

        info1( bs( result ) + " Sell zone trigger check" );
        if( logRootCause ) {
            info2( bs( didMACDNegZoneStart ) + " MACD negative start" ) ;
            info2( bs( emaDownCrossoverRes ) + " EMA down crossover" ) ;
            info2( bs( ema20Jump           ) + " EMA 20 diff < " + SELL_ACTIVATION_EMA_THR + "% in " + ACTIVATION_TEST_GAP + " days" ) ;
        }

        return result ;
    }
    
    @Override
    protected boolean isBuyConditionMet( int index ) {
        
        int     activationAge   = super.getNumDaysIntoEntryZone() ;
        double  pctThreshold    = BUY_SIGNAL_EMA_THR + (activationAge-1)*0.125 ;
        
        boolean isADXUpTrending   = adxUpTrend.isTriggered( index ) ;
        boolean isADXSignalStrong = adxStrength.isTriggered( index ) ;
        
        boolean ema5JumpInActivationZone = hasValuePctChanged( "EMA jump",
                                                     ema5, ema5, index,
                                                     activationAge,
                                                     pctThreshold ) ;
        
        boolean ema5JumpInLast2Days = hasValuePctChanged( "EMA jump",
                                                     ema5, ema5, index,
                                                     2,
                                                     1 ) ;
        
        boolean cpJumpInLast2Days = hasValuePctChanged( "CP Jump",
                                                     cp, cp, index, 1, 1 ) ;
        
        boolean result = ema5JumpInActivationZone &&
                         ema5JumpInLast2Days &&
                         cpJumpInLast2Days &&
                         isADXUpTrending &&
                         isADXSignalStrong ;

        info1( bs( result ) + " Buy condition check" );
        if( logRootCause ) {
            info2( bs( isADXUpTrending    ) + " ADX up trend" ) ;
            info2( bs( isADXSignalStrong        ) + " ADX strength > 25" ) ;
            info2( bs( cpJumpInLast2Days        ) + " CP > 1% in 1 days" ) ;
            info2( bs( ema5JumpInActivationZone ) + " EMA jump > " +
                    pctThreshold + "% in " + activationAge + " days" ) ;
            info2( bs( ema5JumpInLast2Days ) + " EMA jump > 1% in 2 days" ) ;
        }
        return result ;
    }
    
    @Override
    protected boolean isSellConditionMet( int index ) {
        
        int activationAge = super.getNumDaysIntoExitZone() ;
        double  pctThreshold    = SELL_SIGNAL_EMA_THR ;
        
        boolean isADXDownTrending = adxDownTrend.isTriggered( index ) ;
        boolean isADXSignalStrong = adxStrength.isTriggered( index ) ;
        
        boolean ema5JumpInActivationZone = hasValuePctChanged( "EMA jump",
                                                        ema5, ema5, index,
                                                        activationAge,
                                                        pctThreshold ) ;
        
        boolean cpJumpInLast2Days = hasValuePctChanged( "CP Jump",
                                    cp, cp, index, 1, -1 ) ;
        
        boolean result = ema5JumpInActivationZone && cpJumpInLast2Days && isADXDownTrending && isADXSignalStrong ;

        info1( bs( result ) + " Sell condition check" );
        if( logRootCause ) {
            info2( bs( isADXDownTrending ) + " ADX down trend" ) ;
            info2( bs( isADXSignalStrong ) + " ADX strength > 25" ) ;
            info2( bs( cpJumpInLast2Days ) + " CP < 1% in 1 days" ) ;
            info2( bs( ema5JumpInActivationZone ) + " EMA jump < " +
                    pctThreshold + "% in " + activationAge + " days" ) ;
        }
        
        return result ;
    }
    
    private boolean hasValuePctChanged( String label, Indicator<Num> series, Indicator<Num> ref,
                                        int index, int gapDays, double pct ) {
        
        double change = series.getValue( index ).doubleValue() -
                        series.getValue( index-gapDays ).doubleValue() ;
        
        double refVal = Objects.requireNonNullElse( ref, series )
                               .getValue( index - gapDays )
                               .doubleValue();
        
        double chgPct = ( change/refVal )*100 ;
        
        return pct > 0 ? chgPct >= pct : chgPct <= pct ;
    }
}
