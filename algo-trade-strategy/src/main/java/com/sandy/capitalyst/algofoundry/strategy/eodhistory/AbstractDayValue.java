package com.sandy.capitalyst.algofoundry.strategy.eodhistory;

import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

public abstract class AbstractDayValue {
    
    @Getter private       int                          seriesIndex ;
    @Getter private final EquityEODHistory.PayloadType payloadType ;
    @Getter private final String                       symbol ;
    @Getter private final Date date ;
    @Getter private final Bar bar ;
    
    protected AbstractDayValue( EquityEODHistory.PayloadType payloadType, Date date, Bar bar, String symbol ) {
        this.payloadType = payloadType ;
        this.symbol = symbol ;
        this.date = date ;
        this.bar = bar ;
    }
    
    void setSeriesIndex( int index ) {
        this.seriesIndex = index ;
    }
}
