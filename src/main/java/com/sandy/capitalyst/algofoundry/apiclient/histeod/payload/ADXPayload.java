package com.sandy.capitalyst.algofoundry.apiclient.histeod.payload;

import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityEODHistory;
import lombok.Getter;

import java.util.Date;

public class ADXPayload extends AbstractDayValuePayload {

    @Getter private final double adx;
    @Getter private final double plusDMI ;
    @Getter private final double minusDMI ;
    
    public ADXPayload( Date date, String symbol,
                       double adx, double plusDMI, double minusDMI ) {

        super( EquityEODHistory.PayloadType.ADX, date, symbol ) ;
        
        this.adx = adx ;
        this.plusDMI = plusDMI ;
        this.minusDMI = minusDMI ;
    }
}
