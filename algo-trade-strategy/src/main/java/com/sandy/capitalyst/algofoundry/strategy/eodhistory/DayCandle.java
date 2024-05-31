package com.sandy.capitalyst.algofoundry.strategy.eodhistory;

import lombok.Data;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.DecimalNum;

import java.io.Serializable;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Data
public class DayCandle implements Serializable {
    
    private Date date ;
    private float open ;
    private float high ;
    private float low ;
    private float close ;
    private long volume ;
    
    public Bar toBar() {
        return BaseBar.builder()
                .timePeriod( Duration.ofDays( 1 ) )
                .endTime( ZonedDateTime.ofInstant( date.toInstant(), ZoneId.of( "Asia/Kolkata" ) ) )
                .openPrice ( DecimalNum.valueOf( open  ) )
                .highPrice ( DecimalNum.valueOf( high  ) )
                .lowPrice  ( DecimalNum.valueOf( low   ) )
                .closePrice( DecimalNum.valueOf( close ) )
                .volume    ( DecimalNum.valueOf( volume ) )
                .build() ;
    }
}
