package com.sandy.capitalyst.algofoundry.strategy.candleseries.dayvalue;

import com.sandy.capitalyst.algofoundry.strategy.candleseries.DayValue;
import com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

import static com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries.* ;

public class RSIDayValue extends DayValue {

    @Getter private final double rsi;
    
    public RSIDayValue( Date date, Bar bar, String symbol, double rsi ) {

        super( DayValueType.RSI, date, bar, symbol ) ;
        this.rsi = rsi ;
    }
}
