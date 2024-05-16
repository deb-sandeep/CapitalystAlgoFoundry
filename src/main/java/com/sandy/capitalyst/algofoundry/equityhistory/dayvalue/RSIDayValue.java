package com.sandy.capitalyst.algofoundry.equityhistory.dayvalue;

import com.sandy.capitalyst.algofoundry.equityhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import lombok.Getter;

import java.util.Date;

public class RSIDayValue extends AbstractDayValue {

    @Getter private final double rsi;
    
    public RSIDayValue( Date date, String symbol, double rsi ) {

        super( EquityEODHistory.PayloadType.RSI, date, symbol ) ;
        this.rsi = rsi ;
    }
}
