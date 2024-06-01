package com.sandy.capitalyst.algofoundry.strategy.series.candleseries;

import com.sandy.capitalyst.algofoundry.strategy.StrategyConfig;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.dayvalue.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.adx.MinusDIIndicator;
import org.ta4j.core.indicators.adx.PlusDIIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;

import java.io.Serializable;
import java.util.*;

import static com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries.IndicatorName.*;

@Slf4j
public class CandleSeries implements Serializable {
    
    public enum DayValueType {
        OHLCV,
        BOLLINGER,
        MACD,
        RSI,
        ADX,
        EMA5
    }

    public enum IndicatorName {
        CLOSING_PRICE,
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
    
    @Getter private final BarSeries barSeries ;
    @Getter private final String symbol ;
    
    @Setter private int maxSeriesLength = Integer.MAX_VALUE ;
    
    private final Map<IndicatorName, Indicator<Num>>       cache      = new HashMap<>() ;
    private final Map<Integer, EMAIndicator>               emaCache   = new HashMap<>() ;
    private final Map<Integer, StandardDeviationIndicator> stDevCache = new HashMap<>() ;

    private final Set<DayValueListener> dayValueListeners = new HashSet<>() ;
    
    private final StrategyConfig config ;
    
    public CandleSeries( String symbol, List<Candle> candles, StrategyConfig config ) {
        this.config = config ;
        this.barSeries = buildBarSeries( candles ) ;
        this.symbol = symbol ;
    }
    
    private BarSeries buildBarSeries( List<Candle> candles ) {
        
        while( candles.size() > config.getMaxCandleSeriesSize() ) { candles.remove( 0 ) ; }
        
        BarSeries series = new BaseBarSeries( symbol ) ;
        candles.forEach( c -> {
            Bar newBar = c.toBar() ;
            if( !series.isEmpty() ) {
                Bar lastSeriesBar = series.getBar( series.getEndIndex() ) ;
                if( lastSeriesBar.getEndTime().isBefore( newBar.getEndTime() ) ) {
                    series.addBar( c.toBar() ) ;
                }
            }
            else {
                series.addBar( c.toBar() ) ;
            }
        } ) ;
        return series ;
    }
    
    public void addDayValueListener( DayValueListener listener ) {
        dayValueListeners.add( listener ) ;
    }
    
    public void removeDayValueListener( DayValueListener listener ) {
        dayValueListeners.remove( listener ) ;
    }
    
    public int getBarCount() {
        return barSeries.getBarCount() ;
    }
    
    public synchronized void emitDayValues( int index, Collection<DayValueType> dayValueTypes ) {
        dayValueTypes.forEach( pType -> emitValue( index, pType ) ) ;
    }
    
    public synchronized void emitValue( int index, DayValueType pType ) {
        
        if( index >= barSeries.getBarCount() ) {
            throw new IllegalArgumentException( "Index is out of bounds." ) ;
        }
        
        DayValue dayValue = buildPayload( index, pType ) ;
        dayValue.setSeriesIndex( index ) ;

        dayValueListeners.forEach( l -> l.handleDayValue( dayValue ) );
    }
    
    public EMAIndicator getEMAIndicator( int window ) {
        return emaCache.computeIfAbsent( window, this::createEMAIndicator ) ;
    }
    
    public StandardDeviationIndicator getStDevIndicator( int window ) {
        return stDevCache.computeIfAbsent( window, this::createStDevIndicator ) ;
    }
    
    public Indicator<Num> ind( IndicatorName key ) {
        Indicator<Num> ind = cache.get( key ) ;
        if( ind == null ) {
            switch( key ) {
                case CLOSING_PRICE -> ind = createClosePriceIndicator() ;
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
    
    private DayValue buildPayload( int index, DayValueType pType ) {
        
        Bar  bar  = barSeries.getBar( index ) ;
        Date date = Date.from( bar.getEndTime().toInstant() ) ;
        
        DayValue payload = null ;
        switch( pType ) {
            case OHLCV     -> payload = buildOHLCVPayload( date, bar ) ;
            case BOLLINGER -> payload = buildBollingerPayload( index, date, bar ) ;
            case MACD      -> payload = buildMACDPayload( index, date, bar ) ;
            case RSI       -> payload = buildRSIPayload( index, date, bar ) ;
            case ADX       -> payload = buildADXPayload( index, date, bar ) ;
            case EMA5      -> payload = buildEMAPayload( index, date, bar, 5 ) ;
        }
        return payload ;
    }
    
    private OHLCVDayValue buildOHLCVPayload( Date date, Bar bar ) {
        return new OHLCVDayValue( date, symbol, bar ) ;
    }
    
    private BollingerBandDayValue buildBollingerPayload( int index, Date date, Bar bar ) {
        return new BollingerBandDayValue( date, bar, symbol,
                                     getIndVal( BOLLINGER_UP, index ),
                                     getIndVal( BOLLINGER_MID, index ),
                                     getIndVal( BOLLINGER_LOW, index ) );
    }
    
    private MACDDayValue buildMACDPayload( int index, Date date, Bar bar ) {
        return new MACDDayValue( date, bar, symbol,
                                getIndVal( MACD, index ),
                                getIndVal( MACD_SIGNAL, index ) ) ;
    }
    
    private RSIDayValue buildRSIPayload( int index, Date date, Bar bar ) {
        return new RSIDayValue( date, bar, symbol, getIndVal( RSI, index ) ) ;
    }
    
    private ADXDayValue buildADXPayload( int index, Date date, Bar bar ) {
        return new ADXDayValue( date, bar, symbol,
                               getIndVal( ADX, index ),
                               getIndVal( ADX_PLUS_DMI, index ),
                               getIndVal( ADX_MINUS_DMI, index ) ) ;
    }
    
    private MADayValue buildEMAPayload( int index, Date date, Bar bar, int windowSz ) {
        return new MADayValue( date, bar, symbol,
                               MADayValue.MAType.EMA,
                               getEMAIndicator( windowSz ).getValue( index )
                                                          .doubleValue() ) ;
    }
    
    private double getIndVal( IndicatorName indName, int index ) {
        return ind( indName ).getValue( index ).doubleValue() ;
    }
    
    private Indicator<Num> createClosePriceIndicator() {
        return new ClosePriceIndicator( this.barSeries ) ;
    }
    
    private EMAIndicator createEMAIndicator( int period ) {
        return new EMAIndicator( ind( CLOSING_PRICE ), period ) ;
    }
    
    private StandardDeviationIndicator createStDevIndicator( int period ) {
        return new StandardDeviationIndicator( ind( CLOSING_PRICE ), period ) ;
    }
    
    private BollingerBandsMiddleIndicator createBollingerMiddleIndicator() {
        return new BollingerBandsMiddleIndicator(
            getEMAIndicator(
                config.getBollingerWindow()
            )
        ) ;
    }
    
    private Indicator<Num> createBollingerUpperIndicator() {
        return new BollingerBandsUpperIndicator(
                (BollingerBandsMiddleIndicator)ind( BOLLINGER_MID ),
                getStDevIndicator( config.getBollingerWindow() ) ) ;
    }
    
    private Indicator<Num> createBollingerLowerIndicator() {
        return new BollingerBandsLowerIndicator(
                (BollingerBandsMiddleIndicator)ind( BOLLINGER_MID ),
                getStDevIndicator( config.getBollingerWindow() ) ) ;
    }
    
    private Indicator<Num> createMACDIndicator() {
        return new MACDIndicator( ind( CLOSING_PRICE ),
                                  config.getMacdShortWindow(),
                                  config.getMacdLongWindow() ) ;
    }
    
    private Indicator<Num> createMACDSignalIndicator() {
        return new EMAIndicator( ind( MACD ),
                                 config.getMacdSignalWindow() ) ;
    }
    
    private Indicator<Num> createRSIIndicator() {
        return new RSIIndicator( ind( CLOSING_PRICE ),
                                 config.getRsiWindow() ) ;
    }

    private Indicator<Num> createADXIndicator() {
        return new ADXIndicator( barSeries,
                                 config.getAdxDIWindow(),
                                 config.getAdxWindow() ) ;
    }

    private Indicator<Num> createADXPlusDMIIndicator() {
        return new PlusDIIndicator( barSeries, config.getAdxDIWindow() ) ;
    }

    private Indicator<Num> createADXMinusDMIIndicator() {
        return new MinusDIIndicator( barSeries, config.getAdxDIWindow() ) ;
    }
}
