package com.sandy.capitalyst.algofoundry.strategy.candleseries.dayvalue;

import com.sandy.capitalyst.algofoundry.strategy.candleseries.DayValue;
import com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

import static com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries.* ;

public class ADXDayValue extends DayValue {

    @Getter private final double adx;
    @Getter private final double plusDMI ;
    @Getter private final double minusDMI ;
    
    public ADXDayValue( Date date, Bar bar, String symbol,
                        double adx, double plusDMI, double minusDMI ) {

        super( DayValueType.ADX, date, bar, symbol ) ;
        
        this.adx = adx ;
        this.plusDMI = plusDMI ;
        this.minusDMI = minusDMI ;
    }
}
