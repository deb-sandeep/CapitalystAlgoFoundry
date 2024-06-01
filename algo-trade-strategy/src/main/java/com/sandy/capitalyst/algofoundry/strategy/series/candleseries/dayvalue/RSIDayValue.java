package com.sandy.capitalyst.algofoundry.strategy.series.candleseries.dayvalue;

import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.DayValue;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

import static com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries.DayValueType.* ;

public class RSIDayValue extends DayValue {

    @Getter private final double rsi;
    
    public RSIDayValue( Date date, Bar bar, String symbol, double rsi ) {

        super( RSI, date, bar, symbol ) ;
        this.rsi = rsi ;
    }
}
