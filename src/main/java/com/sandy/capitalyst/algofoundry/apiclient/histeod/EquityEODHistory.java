package com.sandy.capitalyst.algofoundry.apiclient.histeod;

import com.sandy.capitalyst.algofoundry.AlgoFoundry;
import com.sandy.capitalyst.algofoundry.EventCatalog;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.numeric.NumericIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;

import java.util.*;

import static com.sandy.capitalyst.algofoundry.core.indicator.IndicatorType.* ;

@Slf4j
public class EquityEODHistory {
    
    private static final int MACD_SHORT_WINDOW  = 12 ;
    private static final int MACD_LONG_WINDOW   = 26 ;
    private static final int MACD_SIGNAL_WINDOW =  9 ;
    
    @Getter private final BarSeries barSeries ;
    @Getter private final String symbol ;
    
    private final Map<String, Indicator<Num>> cache = new HashMap<>() ;
    
    EquityEODHistory( String symbol, BarSeries barSeries ) {
        this.barSeries = barSeries ;
        this.symbol = symbol ;
    }
    
    public Indicator<Num> ind( String key ) {
        Indicator<Num> ind = cache.get( key ) ;
        if( ind == null ) {
            switch( key ) {
                case CLOSING_PRICE -> ind = createClosePriceIndicator() ;
                case SMA_20        -> ind = createSMAIndicator( 20 ) ;
                case STDEV_20      -> ind = createStDevIndicator( 20 ) ;
                case BOLLINGER_LOW -> ind = createBollingerLowerIndicator() ;
                case BOLLINGER_UP  -> ind = createBollingerUpperIndicator() ;
                case BOLLINGER_MID -> ind = createBollingerMiddleIndicator() ;
                case MACD          -> ind = createMACDIndicator() ;
                case MACD_SIGNAL   -> ind = createMACDSignalIndicator() ;
                case MACD_HIST     -> ind = createMACDHistogramIndicator() ;
            }
            cache.put( key, ind ) ;
        }
        return ind ;
    }
    
    public synchronized void emitValues( int index, Collection<String> indNames ) {
        indNames.forEach( ind -> emitValue( index, ind ) ) ;
    }
    
    public synchronized void emitValue( int index, String indName ) {
        
        if( index >= barSeries.getBarCount() ) {
            throw new IllegalArgumentException( "Index is out of bounds." ) ;
        }
        
        Bar  bar  = barSeries.getBar( index ) ;
        Date date = Date.from( bar.getEndTime().toInstant() ) ;
        
        Indicator<Num> ind = ind( indName ) ;
        if( ind == null ) {
            throw new IllegalArgumentException( "Indicator '" + indName + "' does not exist." ) ;
        }
        
        AlgoFoundry.getBus()
                .publishEvent( EventCatalog.EVT_INDICATOR_DAY_VALUE,
                        new IndicatorDayValue( symbol, indName, date,
                                ind.getValue( index )
                                        .doubleValue() ) ) ;
    }
    
    public int getBarCount() {
        return barSeries.getBarCount() ;
    }

    private Indicator<Num> createClosePriceIndicator() {
        return new ClosePriceIndicator( this.barSeries ) ;
    }
    
    private Indicator<Num> createSMAIndicator( int period ) {
        return new SMAIndicator( ind( CLOSING_PRICE ), period ) ;
    }
    
    private Indicator<Num> createStDevIndicator( int period ) {
        return new StandardDeviationIndicator( ind( CLOSING_PRICE ), period ) ;
    }
    
    private BollingerBandsMiddleIndicator createBollingerMiddleIndicator() {
        return new BollingerBandsMiddleIndicator( ind( SMA_20 ) ) ;
    }
    
    private Indicator<Num> createBollingerUpperIndicator() {
        return new BollingerBandsUpperIndicator(
                (BollingerBandsMiddleIndicator)ind( BOLLINGER_MID ),
                ind( STDEV_20 ) ) ;
    }
    
    private Indicator<Num> createBollingerLowerIndicator() {
        return new BollingerBandsLowerIndicator(
                (BollingerBandsMiddleIndicator)ind( BOLLINGER_MID ),
                ind( STDEV_20 ) ) ;
    }
    
    private Indicator<Num> createMACDIndicator() {
        return new MACDIndicator( ind( CLOSING_PRICE ), MACD_SHORT_WINDOW, MACD_LONG_WINDOW ) ;
    }
    
    private Indicator<Num> createMACDSignalIndicator() {
        return new EMAIndicator( ind(MACD), MACD_SIGNAL_WINDOW ) ;
    }
    
    private Indicator<Num> createMACDHistogramIndicator() {
        return NumericIndicator.of( ind( MACD ) ).minus( ind( MACD_SIGNAL ) ) ;
    }
}
