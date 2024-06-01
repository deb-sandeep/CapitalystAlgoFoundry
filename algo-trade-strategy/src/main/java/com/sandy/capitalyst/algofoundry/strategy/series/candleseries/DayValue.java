package com.sandy.capitalyst.algofoundry.strategy.series.candleseries;

import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

import static com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries.* ;

public abstract class DayValue {
    
    @Getter private       int          seriesIndex ;
    @Getter private final DayValueType dayValueType;
    @Getter private final String       symbol ;
    @Getter private final Date         date ;
    @Getter private final Bar          bar ;
    
    protected DayValue( DayValueType type, Date date, Bar bar, String symbol ) {
        this.dayValueType = type;
        this.symbol = symbol ;
        this.date = date ;
        this.bar = bar ;
    }
    
    void setSeriesIndex( int index ) {
        this.seriesIndex = index ;
    }
}
