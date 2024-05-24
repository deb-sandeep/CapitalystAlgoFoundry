package com.sandy.capitalyst.algofoundry.eodhistory.dayvalue;

import com.sandy.capitalyst.algofoundry.eodhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.eodhistory.EquityEODHistory;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

public class MADayValue extends AbstractDayValue {

    public enum MAType { SMA, EMA }
    
    @Getter private final MAType type ;
    @Getter private final double value ;
    
    public MADayValue( Date date, Bar bar, String symbol, MAType type, double value ) {

        super( EquityEODHistory.PayloadType.RSI, date, bar, symbol ) ;
        this.type = type ;
        this.value = value ;
    }
}
