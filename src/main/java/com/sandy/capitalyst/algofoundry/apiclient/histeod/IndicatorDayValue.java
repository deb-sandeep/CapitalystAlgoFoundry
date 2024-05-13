package com.sandy.capitalyst.algofoundry.apiclient.histeod;

import lombok.Data;
import lombok.Getter;

import java.util.Date;

public class IndicatorDayValue {
    
    @Getter private String symbol ;
    @Getter private String indicatorName ;
    @Getter private Date date ;
    @Getter private double value ;
    
    IndicatorDayValue( String symbol, String indicator, Date date, double value ) {
        this.symbol = symbol ;
        this.indicatorName = indicator ;
        this.date = date ;
        this.value = value ;
    }
}
