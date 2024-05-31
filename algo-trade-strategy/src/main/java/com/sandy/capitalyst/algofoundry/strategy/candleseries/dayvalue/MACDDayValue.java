package com.sandy.capitalyst.algofoundry.strategy.candleseries.dayvalue;

import com.sandy.capitalyst.algofoundry.strategy.candleseries.DayValue;
import com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

import static com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries.* ;

public class MACDDayValue extends DayValue {

    @Getter private final double macd;
    @Getter private final double signal ;
    @Getter private final double histogramValue ;
    
    public MACDDayValue( Date date, Bar bar, String symbol,
                         double macdValue, double signalValue ) {

        super( DayValueType.MACD, date, bar, symbol ) ;
        
        this.macd = macdValue ;
        this.signal = signalValue ;
        this.histogramValue = macdValue - signalValue ;
    }
}
