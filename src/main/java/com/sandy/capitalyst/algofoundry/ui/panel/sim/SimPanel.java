package com.sandy.capitalyst.algofoundry.ui.panel.sim;

import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityHistEODAPIClient;
import com.sandy.capitalyst.algofoundry.core.ui.IndicatorChart;
import lombok.extern.slf4j.Slf4j;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.ui.SeriesUtil.*;

@Slf4j
public class SimPanel extends JPanel {
    
    private final String           symbol ;
    private final EquityEODHistory history;
    
    private IndicatorChart chart = null ;
    private List<String> seriesKeys = new ArrayList<>() ;
    
    public SimPanel( String symbol ) throws Exception {
        this.symbol = symbol ;
        this.history = new EquityHistEODAPIClient().getEquityEODHistory( symbol ) ;
        this.chart = new IndicatorChart( symbol, 260 ) ;
        
        addSeriesKey( SN_CLOSING_PRICE ) ;
        addSeriesKey( SN_BOLLINGER_LOW ) ;
        addSeriesKey( SN_BOLLINGER_UP ) ;
        addSeriesKey( SN_BOLLINGER_MID ) ;

        setUpUI() ;
    }
    
    public synchronized void addSeriesKey( String seriesKey ) {
        this.seriesKeys.add( seriesKey ) ;
        this.chart.addSeries( new TimeSeries( seriesKey ) ) ;
    }
    
    public synchronized void removeSeriesKey( String seriesKey ) {
        this.seriesKeys.remove( seriesKey ) ;
        this.chart.removeSeries( seriesKey ) ;
    }
    
    private void setUpUI() {
        
        setLayout( new BorderLayout() ) ;
        add( chart, BorderLayout.CENTER ) ;
        
        new Thread() {
            public void run() {
                
                BarSeries barSeries = history.getBarSeries() ;
                for( int i=0; i<barSeries.getBarCount(); i++ ) {
                    
                    Bar bar = barSeries.getBar( i ) ;
                    Date date = Date.from( bar.getEndTime().toInstant() ) ;
                    
                    int finalI = i;
                    synchronized( SimPanel.this ) {
                        seriesKeys.forEach( k -> addTimeSeriesValue( date, finalI, k ) ) ;
                    }
                    
                    try {
                        Thread.sleep( 50 ) ;
                    }
                    catch( InterruptedException e ) {
                        throw new RuntimeException( e ) ;
                    }
                }
            }
        }.start() ;
    }
    
    private void addTimeSeriesValue( Date date, int index, String seriesKey ) {
        Indicator<Num> ind = history.getIndicator( seriesKey ) ;
        chart.addValue( seriesKey, date, ind.getValue( index ).doubleValue() ) ;
    }
    
    private static TimeSeries buildChartBarSeries( BarSeries barSeries,
                                                   Indicator<Num> indicator,
                                                   String name ) {
        
        TimeSeries timeSeries = new TimeSeries( name );
        for( int i = 0; i < barSeries.getBarCount(); i++ ) {
            Bar bar = barSeries.getBar(i) ;
            timeSeries.add( new Day( Date.from(bar.getEndTime().toInstant())),
                                 indicator.getValue(i).doubleValue() ) ;
        }
        return timeSeries;
    }
}
