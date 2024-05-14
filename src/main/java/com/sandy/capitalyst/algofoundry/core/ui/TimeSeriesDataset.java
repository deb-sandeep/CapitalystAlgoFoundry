package com.sandy.capitalyst.algofoundry.core.ui;

import lombok.Getter;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimeSeriesDataset extends TimeSeriesCollection {
    
    private final Map<String, TimeSeries> timeSeriesMap = new HashMap<>() ;
    
    @Getter private final String name ;
    
    public TimeSeriesDataset( String name ) {
        this.name = name ;
    }
    
    public boolean containsIndicator( String name ) {
        return timeSeriesMap.containsKey( name ) ;
    }
    
    public void addSeries( TimeSeries series ) {
        super.addSeries( series ) ;
        this.timeSeriesMap.put( (String)series.getKey(), series ) ;
    }
    
    public void addValue( String indicatorName, Date date, double value ) {
        if( containsIndicator( indicatorName ) ) {
            TimeSeries series = getSeries( indicatorName ) ;
            series.add( new Day(date), value ) ;
        }
    }
}
