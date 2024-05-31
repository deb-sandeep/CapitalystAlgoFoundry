package com.sandy.capitalyst.algofoundry.strategy.eodhistory.dayvalue;

import com.sandy.capitalyst.algofoundry.strategy.eodhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.strategy.eodhistory.EquityEODHistory;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

public class MACDDayValue extends AbstractDayValue {

    @Getter private final double macd;
    @Getter private final double signal ;
    @Getter private final double histogramValue ;
    
    public MACDDayValue( Date date, Bar bar, String symbol,
                         double macdValue, double signalValue ) {

        super( EquityEODHistory.PayloadType.MACD, date, bar, symbol ) ;
        
        this.macd = macdValue ;
        this.signal = signalValue ;
        this.histogramValue = macdValue - signalValue ;
    }
}
