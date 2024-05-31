package com.sandy.capitalyst.algofoundry.strategy.candleseries.dayvalue;

import com.sandy.capitalyst.algofoundry.strategy.candleseries.DayValue;
import com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

import static com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries.* ;

public class MADayValue extends DayValue {

    public enum MAType { SMA, EMA }
    
    @Getter private final MAType type ;
    @Getter private final double value ;
    
    public MADayValue( Date date, Bar bar, String symbol, MAType type, double value ) {

        super( DayValueType.RSI, date, bar, symbol ) ;
        this.type = type ;
        this.value = value ;
    }
}
