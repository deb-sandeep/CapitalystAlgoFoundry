package com.sandy.capitalyst.algofoundry.strategy.series.candleseries.dayvalue;

import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.DayValue;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

import static com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries.DayValueType.* ;

public class BollingerBandDayValue extends DayValue {

    @Getter private final double high ;
    @Getter private final double mid ;
    @Getter private final double low ;
    
    public BollingerBandDayValue( Date date, Bar bar, String symbol,
                                  double high, double mid, double low ) {

        super( BOLLINGER, date, bar, symbol ) ;
        
        this.high = high ;
        this.low  = low ;
        this.mid  = mid ;
    }
}
