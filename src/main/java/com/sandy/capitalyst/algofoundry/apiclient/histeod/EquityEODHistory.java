package com.sandy.capitalyst.algofoundry.apiclient.histeod;

import com.sandy.capitalyst.algofoundry.AlgoFoundry;
import com.sandy.capitalyst.algofoundry.apiclient.histeod.payload.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.adx.MinusDIIndicator;
import org.ta4j.core.indicators.adx.PlusDIIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;

import javax.swing.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.sandy.capitalyst.algofoundry.EventCatalog.EVT_INDICATOR_DAY_VALUE;
import static com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityEODHistory.IndicatorName.*;

@Slf4j
public class EquityEODHistory {
    
    public enum PayloadType {
        OHLCV,
        BOLLINGER,
        MACD,
        RSI,
        ADX
    }

    public enum IndicatorName {
        CLOSING_PRICE,
        SMA_20,
        STDEV_20,
        BOLLINGER_UP,
        BOLLINGER_MID,
        BOLLINGER_LOW,
        MACD,
        MACD_SIGNAL,
        RSI,
        ADX,
        ADX_PLUS_DMI,
        ADX_MINUS_DMI
    }
    
    private static final int MACD_SHORT_WINDOW  = 12 ;
    private static final int MACD_LONG_WINDOW   = 26 ;
    private static final int MACD_SIGNAL_WINDOW =  9 ;
    private static final int RSI_WINDOW_SIZE    = 14 ;
    private static final int ADX_WINDOW_SIZE    = 14 ;
    private static final int ADX_DI_WINDOW_SIZE = 14 ;
    
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
        
        SwingUtilities.invokeLater( () ->
            AlgoFoundry.getBus()
                       .publishEvent( EVT_INDICATOR_DAY_VALUE,
                                      payload )
        ) ;
    }
    
    private AbstractDayValuePayload buildPayload( int index, PayloadType pType ) {
        
        Bar  bar  = barSeries.getBar( index ) ;
        Date date = Date.from( bar.getEndTime().toInstant() ) ;
        
        AbstractDayValuePayload payload = null ;
        switch( pType ) {
            case OHLCV     -> payload = buildOHLCVPayload( date, bar ) ;
            case BOLLINGER -> payload = buildBollingerPayload( index, date ) ;
            case MACD      -> payload = buildMACDPayload( index, date ) ;
            case RSI       -> payload = buildRSIPayload( index, date ) ;
            case ADX       -> payload = buildADXPayload( index, date ) ;
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
    
    private RSIPayload buildRSIPayload( int index, Date date ) {
        return new RSIPayload( date, symbol, getIndVal( RSI, index ) ) ;
    }
    
    private ADXPayload buildADXPayload( int index, Date date ) {
        return new ADXPayload( date, symbol,
                               getIndVal( ADX, index ),
                               getIndVal( ADX_PLUS_DMI, index ),
                               getIndVal( ADX_MINUS_DMI, index ) ) ;
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
                case RSI           -> ind = createRSIIndicator() ;
                case ADX           -> ind = createADXIndicator() ;
                case ADX_PLUS_DMI  -> ind = createADXPlusDMIIndicator() ;
                case ADX_MINUS_DMI -> ind = createADXMinusDMIIndicator() ;
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
    
    private Indicator<Num> createRSIIndicator() {
        return new RSIIndicator( ind( CLOSING_PRICE ), RSI_WINDOW_SIZE ) ;
    }

    private Indicator<Num> createADXIndicator() {
        return new ADXIndicator( barSeries, ADX_DI_WINDOW_SIZE, ADX_WINDOW_SIZE ) ;
    }

    private Indicator<Num> createADXPlusDMIIndicator() {
        return new PlusDIIndicator( barSeries, ADX_DI_WINDOW_SIZE ) ;
    }

    private Indicator<Num> createADXMinusDMIIndicator() {
        return new MinusDIIndicator( barSeries, ADX_DI_WINDOW_SIZE ) ;
    }
}
