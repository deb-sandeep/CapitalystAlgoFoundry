package com.sandy.capitalyst.algofoundry.eodhistory;

import lombok.Getter;

import java.util.Date;

import static com.sandy.capitalyst.algofoundry.eodhistory.EquityEODHistory.* ;

public abstract class AbstractDayValue {
    
    @Getter private int seriesIndex ;
    @Getter private final PayloadType payloadType ;
    @Getter private final String symbol ;
    @Getter private final Date date ;
    
    protected AbstractDayValue( PayloadType payloadType, Date date, String symbol ) {
        this.payloadType = payloadType ;
        this.symbol = symbol ;
        this.date = date ;
    }
    
    void setSeriesIndex( int index ) {
        this.seriesIndex = index ;
    }
}
