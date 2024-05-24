package com.sandy.capitalyst.algofoundry.eodhistory.dayvalue;

import com.sandy.capitalyst.algofoundry.eodhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.eodhistory.EquityEODHistory;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

public class BollingerBandDayValue extends AbstractDayValue {

    @Getter private final double high ;
    @Getter private final double mid ;
    @Getter private final double low ;
    
    public BollingerBandDayValue( Date date, Bar bar, String symbol,
                                  double high, double mid, double low ) {

        super( EquityEODHistory.PayloadType.BOLLINGER, date, bar, symbol ) ;
        
        this.high = high ;
        this.low  = low ;
        this.mid  = mid ;
    }
}
