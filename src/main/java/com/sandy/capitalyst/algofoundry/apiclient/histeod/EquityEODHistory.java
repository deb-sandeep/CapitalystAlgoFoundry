package com.sandy.capitalyst.algofoundry.apiclient.histeod;

import com.sandy.capitalyst.algofoundry.ui.SeriesUtil;
import lombok.Getter;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;

import java.util.HashMap;
import java.util.Map;

import static com.sandy.capitalyst.algofoundry.ui.SeriesUtil.* ;

public class EquityEODHistory {
    
    @Getter private final BarSeries barSeries ;
    
    private final Map<String, Indicator<Num>> cache = new HashMap<>() ;
    
    EquityEODHistory( BarSeries barSeries ) {
        this.barSeries = barSeries ;
    }
    
    public Indicator<Num> getIndicator( String key ) {
        switch( key ) {
            case SN_CLOSING_PRICE -> { return getClosePriceIndicator() ; }
            case SN_BOLLINGER_LOW -> { return getBollingerLowerIndicator() ; }
            case SN_BOLLINGER_UP  -> { return getBollingerUpperIndicator() ; }
            case SN_BOLLINGER_MID -> { return getBollingerMiddleIndicator() ; }
        }
        return null;
    }
    
    public Indicator<Num> getClosePriceIndicator() {
        final String KEY = SN_CLOSING_PRICE ;
        Indicator<Num> ind = cache.get( KEY ) ;
        if( ind == null ) {
            ind = new ClosePriceIndicator( this.barSeries ) ;
            cache.put( KEY, ind ) ;
        }
        return ind ;
    }
    
    public Indicator<Num> getSMAIndicator( int period ) {
        final String KEY = "SMA-" + period ;
        Indicator<Num> ind = cache.get( KEY ) ;
        if( ind == null ) {
            ind = new SMAIndicator( getClosePriceIndicator(), period ) ;
            cache.put( KEY, ind ) ;
        }
        return ind ;
    }
    
    public Indicator<Num> getStdDevIndicator( int period ) {
        final String KEY = "StdDev-" + period ;
        Indicator<Num> ind = cache.get( KEY ) ;
        if( ind == null ) {
            ind = new StandardDeviationIndicator( getClosePriceIndicator(), period ) ;
            cache.put( KEY, ind ) ;
        }
        return ind ;
    }
    
    public BollingerBandsMiddleIndicator getBollingerMiddleIndicator() {
        final String KEY = SN_BOLLINGER_MID ;
        Indicator<Num> ind = cache.get( KEY ) ;
        if( ind == null ) {
            ind = new BollingerBandsMiddleIndicator( getSMAIndicator( 20 ) ) ;
            cache.put( KEY, ind ) ;
        }
        return (BollingerBandsMiddleIndicator)ind ;
    }
    
    public Indicator<Num> getBollingerUpperIndicator() {
        final String KEY = SN_BOLLINGER_UP ;
        Indicator<Num> ind = cache.get( KEY ) ;
        if( ind == null ) {
            ind = new BollingerBandsUpperIndicator(
                            getBollingerMiddleIndicator(),
                            getStdDevIndicator( 20 ) ) ;
            cache.put( KEY, ind ) ;
        }
        return ind ;
    }
    
    public Indicator<Num> getBollingerLowerIndicator() {
        final String KEY = SN_BOLLINGER_LOW ;
        Indicator<Num> ind = cache.get( KEY ) ;
        if( ind == null ) {
            ind = new BollingerBandsLowerIndicator(
                            getBollingerMiddleIndicator(),
                            getStdDevIndicator( 20 ) ) ;
            cache.put( KEY, ind ) ;
        }
        return ind ;
    }
}
