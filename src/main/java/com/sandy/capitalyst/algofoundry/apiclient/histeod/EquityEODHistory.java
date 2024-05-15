package com.sandy.capitalyst.algofoundry.apiclient.histeod;

import com.sandy.capitalyst.algofoundry.AlgoFoundry;
import com.sandy.capitalyst.algofoundry.EventCatalog;
import com.sandy.capitalyst.algofoundry.apiclient.histeod.payload.AbstractDayValuePayload;
import com.sandy.capitalyst.algofoundry.apiclient.histeod.payload.BollingerPayload;
import com.sandy.capitalyst.algofoundry.apiclient.histeod.payload.MACDPayload;
import com.sandy.capitalyst.algofoundry.apiclient.histeod.payload.OHLCVPayload;
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
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;

import java.util.*;

import static com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityEODHistory.IndicatorName.* ;

@Slf4j
public class EquityEODHistory {
    
    public enum PayloadType {
        OHLCV,
        BOLLINGER,
        MACD
    }

    public enum IndicatorName {
        CLOSING_PRICE,
        SMA_20,
        STDEV_20,
        BOLLINGER_UP,
        BOLLINGER_MID,
        BOLLINGER_LOW,
        MACD,
        MACD_SIGNAL
    }
    
    private static final int MACD_SHORT_WINDOW  = 12 ;
    private static final int MACD_LONG_WINDOW   = 26 ;
    private static final int MACD_SIGNAL_WINDOW =  9 ;
    
    @Getter private final BarSeries barSeries ;
    @Getter private final String symbol ;
    
    private final Map<IndicatorName, Indicator<Num>> cache = new HashMap<>() ;
    
    EquityEODHistory( String symbol, BarSeries barSeries ) {
        this.barSeries = barSeries ;
        this.symbol = symbol ;
    }
    
    public int getBarCount() {
        return barSeries.getBarCount() ;
    }
    
    public synchronized void emitDayValues( int index, Collection<PayloadType> payloadTypes ) {
        payloadTypes.forEach( pType -> emitValue( index, pType ) ) ;
    }
    
    public synchronized void emitValue( int index, PayloadType pType ) {
        
        if( index >= barSeries.getBarCount() ) {
            throw new IllegalArgumentException( "Index is out of bounds." ) ;
        }
        
        AbstractDayValuePayload payload = buildPayload( index, pType ) ;
        
        AlgoFoundry.getBus()
                   .publishEvent( EventCatalog.EVT_INDICATOR_DAY_VALUE,
                                  payload ) ;
    }
    
    private AbstractDayValuePayload buildPayload( int index, PayloadType pType ) {
        
        Bar  bar  = barSeries.getBar( index ) ;
        Date date = Date.from( bar.getEndTime().toInstant() ) ;
        
        AbstractDayValuePayload payload = null ;
        switch( pType ) {
            case OHLCV     -> payload = buildOHLCVPayload( date, bar ) ;
            case BOLLINGER -> payload = buildBollingerPayload( index, date ) ;
            case MACD      -> payload = buildMACDPayload( index, date ) ;
        }
        return payload ;
    }
    
    private OHLCVPayload buildOHLCVPayload( Date date, Bar bar ) {
        return new OHLCVPayload( date, symbol, bar ) ;
    }
    
    private BollingerPayload buildBollingerPayload( int index, Date date ) {
        return new BollingerPayload( date, symbol,
                                     getIndVal( BOLLINGER_UP, index ),
                                     getIndVal( BOLLINGER_MID, index ),
                                     getIndVal( BOLLINGER_LOW, index ) );
    }
    
    private MACDPayload buildMACDPayload( int index, Date date ) {
        return new MACDPayload( date, symbol,
                                getIndVal( MACD, index ),
                                getIndVal( MACD_SIGNAL, index ) ) ;
    }
    
    private double getIndVal( IndicatorName indName, int index ) {
        return ind( indName ).getValue( index ).doubleValue() ;
    }
    
    private Indicator<Num> ind( IndicatorName key ) {
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
            }
            cache.put( key, ind ) ;
        }
        return ind ;
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
        return new EMAIndicator( ind( MACD ), MACD_SIGNAL_WINDOW ) ;
    }
}
