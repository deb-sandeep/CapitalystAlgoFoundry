package com.sandy.capitalyst.algofoundry.apiclient.histeod.payload;

import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityEODHistory;
import lombok.Getter;

import java.util.Date;

public class MACDPayload extends AbstractDayValuePayload {

    @Getter private double macd;
    @Getter private double signal ;
    @Getter private double histogramValue ;
    
    public MACDPayload( Date date, String symbol,
                        double macdValue, double signalValue ) {

        super( EquityEODHistory.PayloadType.MACD, date, symbol ) ;
        
        this.macd = macdValue ;
        this.signal = signalValue ;
        this.histogramValue = macdValue - signalValue ;
    }
}