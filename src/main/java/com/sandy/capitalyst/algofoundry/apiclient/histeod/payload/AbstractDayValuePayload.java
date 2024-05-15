package com.sandy.capitalyst.algofoundry.apiclient.histeod.payload;

import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityEODHistory;
import lombok.Getter;

import java.util.Date;

import static com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityEODHistory.* ;

public abstract class AbstractDayValuePayload {
    
    @Getter private final PayloadType payloadType ;
    @Getter private final String symbol ;
    @Getter private final Date date ;
    
    protected AbstractDayValuePayload( PayloadType payloadType, Date date, String symbol ) {
        this.payloadType = payloadType ;
        this.symbol = symbol ;
        this.date = date ;
    }
}
