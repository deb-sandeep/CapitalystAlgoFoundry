package com.sandy.capitalyst.algofoundry.apiclient.histeod.payload;

import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityEODHistory;
import lombok.Getter;

import java.util.Date;

public class BollingerPayload extends AbstractDayValuePayload {

    @Getter private double high ;
    @Getter private double mid ;
    @Getter private double low ;
    
    public BollingerPayload( Date date, String symbol,
                             double high, double mid, double low ) {

        super( EquityEODHistory.PayloadType.BOLLINGER, date, symbol ) ;
        
        this.high = high ;
        this.low  = low ;
        this.mid  = mid ;
    }
}
