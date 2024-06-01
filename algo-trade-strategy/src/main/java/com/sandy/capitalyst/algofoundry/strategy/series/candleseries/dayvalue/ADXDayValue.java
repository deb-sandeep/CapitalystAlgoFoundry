package com.sandy.capitalyst.algofoundry.strategy.series.candleseries.dayvalue;

import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.DayValue;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

import static com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries.DayValueType.* ;

public class ADXDayValue extends DayValue {

    @Getter private final double adx;
    @Getter private final double plusDMI ;
    @Getter private final double minusDMI ;
    
    public ADXDayValue( Date date, Bar bar, String symbol,
                        double adx, double plusDMI, double minusDMI ) {

        super( ADX, date, bar, symbol ) ;
        
        this.adx = adx ;
        this.plusDMI = plusDMI ;
        this.minusDMI = minusDMI ;
    }
}
