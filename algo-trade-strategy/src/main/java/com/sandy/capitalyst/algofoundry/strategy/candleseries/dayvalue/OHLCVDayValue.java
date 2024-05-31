package com.sandy.capitalyst.algofoundry.strategy.candleseries.dayvalue;

import com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries;
import com.sandy.capitalyst.algofoundry.strategy.candleseries.DayValue;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

import static com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries.* ;

public class OHLCVDayValue extends DayValue {

    @Getter private double open ;
    @Getter private double high ;
    @Getter private double low ;
    @Getter private double close ;
    @Getter private long   volume ;
    
    public OHLCVDayValue( Date date, String symbol, Bar bar ) {

        super( DayValueType.OHLCV, date, bar, symbol ) ;
        
        this.open   = bar.getOpenPrice().doubleValue() ;
        this.high   = bar.getHighPrice().doubleValue() ;
        this.low    = bar.getLowPrice().doubleValue() ;
        this.close  = bar.getClosePrice().doubleValue() ;
        this.volume = bar.getVolume().longValue() ;
    }
}
