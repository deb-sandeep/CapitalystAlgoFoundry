package com.sandy.capitalyst.algofoundry.strategy;

import com.sandy.capitalyst.algofoundry.strategy.util.HParameter;
import lombok.Data;

import java.io.Serializable;

@Data
public class StrategyConfig implements Serializable {

    // Configuration values for technical indicators
    private int macdShortWindow  = 12 ;
    private int macdLongWindow   = 26 ;
    private int macdSignalWindow =  9 ;
    private int rsiWindow        = 14 ;
    private int adxWindow        = 14 ;
    private int adxDIWindow      = 14 ;
    private int bollingerWindow  = 20 ;
    
    /**
     * Maximum number of candle series data points to be considered for
     * simulation. The CandleSeries will discard all data points which
     * are older than the specified number of days from the latest date.
     */
    private int maxCandleSeriesSize = 260 ;
    
    /**
     * The number of days the buy/sell zone will stay active once triggered.
     * Used by the ZonedSignalStrategy class
     */
    //@HParameter( min=1, max=10 )
    private int activeZoneMaxAge = 5 ;
    
    /**
     * The number of blackout days at the start of a candle series. This is
     * because most of the indicators give their true value once their window
     * size is achieved.
     */
    private int initialBlackoutNumDays = 20 ;
}
