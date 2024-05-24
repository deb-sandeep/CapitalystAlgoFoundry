package com.sandy.capitalyst.algofoundry.apiclient.equitymeta;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class EquityMeta implements Serializable {

    // From Equity Master
    private String isin           = null ;
    private String symbol         = null ;
    private String name           = null ;
    private String description    = null ;
    private String industry       = null ;
    private String sector         = null ;
    
    // From Indicators
    private Date  asOnDate        = null ;
    private float currentPrice    = 0 ;
    private float high52          = 0 ;
    private float low52           = 0 ;
    private float marketCap       = 0 ;
    private long  volWeekAvg      = 0 ;
    private float pe              = 0 ;
    private float sectorPE        = 0 ;
    private float eps             = 0 ;
    private float pb              = 0 ;
    private float opmPct          = 0 ;
    private float dividendYeild   = 0 ;
    private int   piotroskiScore  = 0 ;
    private float grahamNumber    = 0 ;
    private int   gFactor         = 0 ;
    private float earningPower    = 0 ;
    private float promoterHolding = 0 ;
    private float publicHolding   = 0 ;
    private float fiiHolding      = 0 ;
    private float chgFiiHolding   = 0 ;
    private float diiHolding      = 0 ;
    private float chgDiiHolding   = 0 ;
    private float dma50           = 0 ;
    private float dma200          = 0 ;
    private float dma50Prev       = 0 ;
    private float dma200Prev      = 0 ;
    private float rsi             = 0 ;
    private float macd            = 0 ;
    private float macdPrev        = 0 ;
    private float macdSignal      = 0 ;
    private float macdSignalPrev  = 0 ;
    
    // TTM Perf
    private Float perf1d  = null ;
    private float perf1w  = 0.0F ;
    private float perf2w  = 0.0F ;
    private float perf1m  = 0.0F ;
    private float perf2m  = 0.0F ;
    private float perf3m  = 0.0F ;
    private float perf4m  = 0.0F ;
    private float perf5m  = 0.0F ;
    private float perf6m  = 0.0F ;
    private float perf7m  = 0.0F ;
    private float perf8m  = 0.0F ;
    private float perf9m  = 0.0F ;
    private float perf10m = 0.0F ;
    private float perf11m = 0.0F ;
    private float perf12m = 0.0F ;
    private float perfFy  = 0.0F ;
}
