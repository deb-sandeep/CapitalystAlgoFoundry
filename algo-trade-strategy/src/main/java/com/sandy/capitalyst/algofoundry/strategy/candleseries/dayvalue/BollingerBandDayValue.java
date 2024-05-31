package com.sandy.capitalyst.algofoundry.strategy.candleseries.dayvalue;

import com.sandy.capitalyst.algofoundry.strategy.candleseries.DayValue;
import com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

import static com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries.* ;

public class BollingerBandDayValue extends DayValue {

    @Getter private final double high ;
    @Getter private final double mid ;
    @Getter private final double low ;
    
    public BollingerBandDayValue( Date date, Bar bar, String symbol,
                                  double high, double mid, double low ) {

        super( DayValueType.BOLLINGER, date, bar, symbol ) ;
        
        this.high = high ;
        this.low  = low ;
        this.mid  = mid ;
    }
}
