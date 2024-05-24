package com.sandy.capitalyst.algofoundry.eodhistory.dayvalue;

import com.sandy.capitalyst.algofoundry.eodhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.eodhistory.EquityEODHistory;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

public class RSIDayValue extends AbstractDayValue {

    @Getter private final double rsi;
    
    public RSIDayValue( Date date, Bar bar, String symbol, double rsi ) {

        super( EquityEODHistory.PayloadType.RSI, date, bar, symbol ) ;
        this.rsi = rsi ;
    }
}
