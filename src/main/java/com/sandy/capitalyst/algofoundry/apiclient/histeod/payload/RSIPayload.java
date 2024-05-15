package com.sandy.capitalyst.algofoundry.apiclient.histeod.payload;

import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityEODHistory;
import lombok.Getter;

import java.util.Date;

public class RSIPayload extends AbstractDayValuePayload {

    @Getter private final double rsi;
    
    public RSIPayload( Date date, String symbol, double rsi ) {

        super( EquityEODHistory.PayloadType.RSI, date, symbol ) ;
        this.rsi = rsi ;
    }
}
