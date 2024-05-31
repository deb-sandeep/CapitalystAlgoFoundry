package com.sandy.capitalyst.algofoundry.strategy.eodhistory.dayvalue;

import com.sandy.capitalyst.algofoundry.strategy.eodhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.eodhistory.AbstractDayValue;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

public class OHLCVDayValue extends AbstractDayValue {

    @Getter private double open ;
    @Getter private double high ;
    @Getter private double low ;
    @Getter private double close ;
    @Getter private long   volume ;
    
    public OHLCVDayValue( Date date, String symbol, Bar bar ) {

        super( EquityEODHistory.PayloadType.OHLCV, date, bar, symbol ) ;
        
        this.open   = bar.getOpenPrice().doubleValue() ;
        this.high   = bar.getHighPrice().doubleValue() ;
        this.low    = bar.getLowPrice().doubleValue() ;
        this.close  = bar.getClosePrice().doubleValue() ;
        this.volume = bar.getVolume().longValue() ;
    }
}