package com.sandy.capitalyst.algofoundry.strategy.impl;

import com.sandy.capitalyst.algofoundry.strategy.impl.util.StringUtil;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries;
import com.sandy.capitalyst.algofoundry.strategy.signal.ZonedSignalStrategy;
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

@Slf4j
public class MySignalStrategy extends ZonedSignalStrategy {
    
    public static final String NAME = "My Strategy" ;
    
    private final Indicator<Num> cp;
    private final EMAIndicator   ema5;
    private final EMAIndicator   ema20;
    
    private MACDHistPositiveStartRule macdPositiveStart;
    private MACDHistNegativeStartRule macdNegStart;
    private EMAUpCrossoverRule        emaUpCrossover ;
    private EMADownCrossoverRule      emaDownCrossover ;
    private ADXUpTrendRule            adxUpTrend ;
    private ADXDownTrendRule          adxDownTrend ;
    private ADXStrengthRule           adxStrength ;
    
    private final MyStrategyConfig config ;

    public MySignalStrategy( CandleSeries history, MyStrategyConfig config ) {
        
        super( history, config ) ;
        this.config = config ;
        
        cp = history.ind( CandleSeries.IndicatorName.CLOSING_PRICE ) ;
        ema5 = history.getEMAIndicator( 5 );
        ema20 = history.getEMAIndicator( 20 ) ;
        
        createRules() ;
    }
    
    private void createRules() {
        macdPositiveStart = new MACDHistPositiveStartRule( candleSeries ) ;
        macdNegStart = new MACDHistNegativeStartRule( candleSeries ) ;
        
        emaUpCrossover = new EMAUpCrossoverRule( candleSeries ) ;
        emaDownCrossover = new EMADownCrossoverRule( candleSeries ) ;
        
        adxUpTrend = new ADXUpTrendRule( candleSeries ) ;
        adxDownTrend = new ADXDownTrendRule( candleSeries ) ;
        
        adxStrength = new ADXStrengthRule( candleSeries, 25 ) ;
    }
    
    @Override
    protected boolean isBuyZoneActivated( int index ) {
        
        boolean didMACDPosZoneStart = macdPositiveStart.isTriggered( index ) ;
        boolean emaUpCrossoverRes   = emaUpCrossover.isTriggered( index ) ;
        boolean ema20Jump           = hasValuePctChanged( "EMA jump",
                                                    ema20, ema20, index,
                                                    config.getZoneActivationTestGap(),
                                                    config.getEma20JumpForBuyZoneActivation() ) ;
        
        boolean result = didMACDPosZoneStart || emaUpCrossoverRes || ema20Jump ;

        info1( StringUtil.bs( result ) + " Buy zone trigger check" );
        if( config.isLogStrategyDecisions() ) {
            info2( StringUtil.bs( didMACDPosZoneStart ) + " MACD positive start" ) ;
            info2( StringUtil.bs( emaUpCrossoverRes   ) + " EMA up crossover" ) ;
            
            info2( StringUtil.bs( ema20Jump ) + " EMA 20 diff > " +
                    config.getEma20JumpForBuyZoneActivation() + "% in " +
                    config.getZoneActivationTestGap() + " days" ) ;
        }
        
        return result ;
    }
    
    @Override
    protected boolean isSellZoneActivated( int index ) {
        
        boolean didMACDNegZoneStart = macdNegStart.isTriggered( index ) ;
        boolean emaDownCrossoverRes = emaDownCrossover.isTriggered( index ) ;
        boolean ema20Jump           = hasValuePctChanged( "EMA jump",
                                                    ema20, ema20, index,
                                                    config.getZoneActivationTestGap(),
                                                    config.getEma20DipForSellZoneActivation() ) ;
        
        boolean result = didMACDNegZoneStart || emaDownCrossoverRes || ema20Jump ;

        info1( StringUtil.bs( result ) + " Sell zone trigger check" );
        if( config.isLogStrategyDecisions() ) {
            info2( StringUtil.bs( didMACDNegZoneStart ) + " MACD negative start" ) ;
            info2( StringUtil.bs( emaDownCrossoverRes ) + " EMA down crossover" ) ;
            
            info2( StringUtil.bs( ema20Jump ) + " EMA 20 diff < " +
                    config.getEma20DipForSellZoneActivation() + "% in " +
                    config.getZoneActivationTestGap() + " days" ) ;
        }

        return result ;
    }
    
    @Override
    protected boolean isBuyConditionMet( int index ) {
        
        int     activationAge   = super.getNumDaysIntoEntryZone() ;
        double  pctThreshold    = config.getEma5JumpForBuySignal() +
                                  (activationAge-1)*config.getEma5JumpIncrementPerDayForBuySignal() ;
        
        boolean isADXUpTrending   = adxUpTrend.isTriggered( index ) ;
        boolean isADXSignalStrong = adxStrength.isTriggered( index ) ;
        
        boolean ema5JumpInActivationZone = hasValuePctChanged( "EMA jump",
                                                     ema5, ema5, index,
                                                     activationAge,
                                                     pctThreshold ) ;
        
        boolean result = ema5JumpInActivationZone &&
                         isADXUpTrending &&
                         isADXSignalStrong ;

        info1( StringUtil.bs( result ) + " Buy condition check" );
        if( config.isLogStrategyDecisions() ) {
            info2( StringUtil.bs( isADXUpTrending          ) + " ADX up trend" ) ;
            info2( StringUtil.bs( isADXSignalStrong        ) + " ADX strength > 25" ) ;
            info2( StringUtil.bs( ema5JumpInActivationZone ) + " EMA jump > " +
                    pctThreshold + "% in " + activationAge + " days" ) ;
        }
        return result ;
    }
    
    @Override
    protected boolean isSellConditionMet( int index ) {
        
        int     activationAge = super.getNumDaysIntoExitZone() ;
        double  pctThreshold  = config.getEma5DipForSellSignal() +
                                (activationAge-1)*config.getEma5DipDecrementPerDayForSellSignal() ;
                                
        boolean isADXDownTrending = adxDownTrend.isTriggered( index ) ;
        boolean isADXSignalStrong = adxStrength.isTriggered( index ) ;
        
        boolean ema5JumpInActivationZone = hasValuePctChanged( "EMA jump",
                                                        ema5, ema5, index,
                                                        activationAge,
                                                        pctThreshold ) ;
        
        boolean result = ema5JumpInActivationZone && isADXDownTrending && isADXSignalStrong ;

        info1( StringUtil.bs( result ) + " Sell condition check" );
        if( config.isLogStrategyDecisions() ) {
            info2( StringUtil.bs( isADXDownTrending ) + " ADX down trend" ) ;
            info2( StringUtil.bs( isADXSignalStrong ) + " ADX strength > 25" ) ;
            info2( StringUtil.bs( ema5JumpInActivationZone ) + " EMA jump < " +
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
