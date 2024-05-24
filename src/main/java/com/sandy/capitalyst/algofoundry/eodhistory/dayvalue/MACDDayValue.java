package com.sandy.capitalyst.algofoundry.eodhistory.dayvalue;

import com.sandy.capitalyst.algofoundry.eodhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.eodhistory.EquityEODHistory;
import lombok.Getter;

import java.util.Date;

public class MACDDayValue extends AbstractDayValue {

    @Getter private final double macd;
    @Getter private final double signal ;
    @Getter private final double histogramValue ;
    
    public MACDDayValue( Date date, String symbol,
                         double macdValue, double signalValue ) {

        super( EquityEODHistory.PayloadType.MACD, date, symbol ) ;
        
        this.macd = macdValue ;
        this.signal = signalValue ;
        this.histogramValue = macdValue - signalValue ;
    }
}
