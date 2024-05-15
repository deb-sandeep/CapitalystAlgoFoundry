package com.sandy.capitalyst.algofoundry.apiclient.histeod.payload;

import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityEODHistory;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

public class OHLCVPayload extends AbstractDayValuePayload {

    @Getter private double open ;
    @Getter private double high ;
    @Getter private double low ;
    @Getter private double close ;
    @Getter private long   volume ;
    
    public OHLCVPayload( Date date, String symbol, Bar bar ) {

        super( EquityEODHistory.PayloadType.OHLCV, date, symbol ) ;
        
        this.open   = bar.getOpenPrice().doubleValue() ;
        this.high   = bar.getHighPrice().doubleValue() ;
        this.low    = bar.getLowPrice().doubleValue() ;
        this.close  = bar.getClosePrice().doubleValue() ;
        this.volume = bar.getVolume().longValue() ;
    }
}
