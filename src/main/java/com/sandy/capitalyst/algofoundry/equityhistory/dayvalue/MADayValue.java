package com.sandy.capitalyst.algofoundry.equityhistory.dayvalue;

import com.sandy.capitalyst.algofoundry.equityhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import lombok.Getter;

import java.util.Date;

public class MADayValue extends AbstractDayValue {

    public enum MAType { SMA, EMA }
    
    @Getter private final MAType type ;
    @Getter private final double value ;
    
    public MADayValue( Date date, String symbol, MAType type, double value ) {

        super( EquityEODHistory.PayloadType.RSI, date, symbol ) ;
        this.type = type ;
        this.value = value ;
    }
}
