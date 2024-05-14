package com.sandy.capitalyst.algofoundry.core.ui;

import com.sandy.capitalyst.algofoundry.apiclient.histeod.IndicatorDayValue;
import com.sandy.capitalyst.algofoundry.core.bus.Event;
import com.sandy.capitalyst.algofoundry.core.bus.EventSubscriber;
import com.sandy.capitalyst.algofoundry.core.indicator.IndicatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.sandy.capitalyst.algofoundry.AlgoFoundry.getBus;
import static com.sandy.capitalyst.algofoundry.AlgoFoundry.getCleaner;
import static com.sandy.capitalyst.algofoundry.AlgoFoundry.getConfig;
import static com.sandy.capitalyst.algofoundry.EventCatalog.EVT_INDICATOR_DAY_VALUE;
import static com.sandy.capitalyst.algofoundry.core.ui.UITheme.*;

@Slf4j
public abstract class IndicatorChart extends JPanel
    implements EventSubscriber {
    
    private JFreeChart       chart      = null ;
    private ChartPanel       chartPanel = null ;
    private XYPlot           plot       = null ;
    private AbstractRenderer renderer   = null ;
    
    private final TimeSeriesCollection timeSeriesColl = new TimeSeriesCollection() ;
    private final Map<String, TimeSeries> timeSeriesMap = new HashMap<>() ;
    
    private final String symbol ;
    private final int xAxisWindowSize ;
    
    public IndicatorChart( String symbol, String yLabel ) {
        this( symbol, null, yLabel ) ;
    }
    
    public IndicatorChart( String symbol, String title, String yLabel ) {
        
        this.xAxisWindowSize = getConfig().getDateWindowSize() ;
        this.symbol = symbol ;
        
        createChart( title, yLabel ) ;
        setLayout( new BorderLayout() ) ;
        add( chartPanel, BorderLayout.CENTER ) ;
        
        getBus().addSubscriberForEventTypes( this, false, EVT_INDICATOR_DAY_VALUE );
        getCleaner().register( this, getCleanerAction() ) ;
    }
    
    public Runnable getCleanerAction() {
        return () -> {
            log.debug( "Cleaning chart." ) ;
            getBus().removeSubscriber( this, EVT_INDICATOR_DAY_VALUE ) ;
        } ;
    }
    
    private void createChart( String title, String yAxisLabel ) {
        
        chart = ChartFactory.createTimeSeriesChart(
                title,
                null,
                yAxisLabel,
                this.timeSeriesColl ) ;
        
        chart.setBackgroundPaint( Color.BLACK ) ;
        chart.removeLegend() ;
        
        configureTitle( title ) ;
        configurePlot() ;
        configureAxes() ;
        
        chartPanel = new ChartPanel( chart ) ;
        chartPanel.setDoubleBuffered( true ) ;
        chartPanel.setFillZoomRectangle( true ) ;
        chartPanel.setMouseWheelEnabled( true ) ;
        chartPanel.setMinimumDrawWidth( 0 ) ;
        chartPanel.setMaximumDrawWidth( Integer.MAX_VALUE ) ;
        chartPanel.setMinimumDrawHeight( 0 ) ;
        chartPanel.setMaximumDrawHeight( Integer.MAX_VALUE ) ;
    }
    
    private void configureTitle( String title ) {
        if( title != null ) {
            chart.setTitle( title ) ;
            chart.getTitle().setPaint( UITheme.CHART_TITLE_COLOR ) ;
            chart.getTitle().setFont( UITheme.CHART_TITLE_FONT ) ;
        }
    }
    
    private void configurePlot() {
        
        plot = ( XYPlot )chart.getPlot() ;
        plot.setBackgroundPaint( Color.BLACK ) ;
        plot.setDomainGridlinePaint( Color.DARK_GRAY.darker() ) ;
        plot.setRangeGridlinePaint( Color.DARK_GRAY.darker() ) ;
        plot.setRangePannable( true ) ;
        plot.setDomainPannable( true ) ;
        
        renderer = ( AbstractRenderer )plot.getRenderer() ;

        AxisSpace space = new AxisSpace() ;
        space.setLeft( 50 ) ;
        plot.setFixedRangeAxisSpace( space ) ;
    }
    
    private void configureAxes() {
        
        ValueAxis yAxis = plot.getRangeAxis() ;
        DateAxis  xAxis = (DateAxis)plot.getDomainAxis() ;
        
        yAxis.setLabelFont( CHART_AXIS_LABEL_FONT ) ;
        yAxis.setTickLabelFont( CHART_AXIS_TICK_FONT ) ;
        yAxis.setTickLabelPaint( CHART_AXIS_TICK_COLOR ) ;
        yAxis.setLabelPaint( CHART_LABEL_COLOR ) ;
        
        xAxis.setLabelFont( CHART_AXIS_LABEL_FONT ) ;
        xAxis.setTickLabelFont( CHART_AXIS_TICK_FONT ) ;
        xAxis.setTickLabelPaint( CHART_AXIS_TICK_COLOR ) ;
        xAxis.setLabelPaint( CHART_LABEL_COLOR ) ;
        
        xAxis.setDateFormatOverride( new SimpleDateFormat( "dd-MMM-yy" ) ) ;
    }
    
    public void addSeries( String seriesKey ) {
        
        if( !timeSeriesMap.containsKey( seriesKey ) ) {
            
            TimeSeries series = new TimeSeries( seriesKey ) ;
            
            timeSeriesMap.put( seriesKey, series ) ;
            timeSeriesColl.addSeries( series ) ;
            
            if( this.xAxisWindowSize > 0 ) {
                series.setMaximumItemCount( this.xAxisWindowSize ) ;
            }
            
            int seriesIndex = timeSeriesColl.getSeriesCount()-1 ;
            renderer.setSeriesPaint( seriesIndex,
                                     IndicatorUtil.getColor( seriesKey ) ) ;
            renderer.setSeriesStroke( seriesIndex,
                                      IndicatorUtil.getStroke( seriesKey ) ) ;
        }
    }
    
    public void clearSeries( String seriesKey ) {
        timeSeriesMap.get( seriesKey ).clear() ;
    }
    
    public void removeSeries( String key ) {
        TimeSeries timeSeries = timeSeriesMap.remove( key ) ;
        if( timeSeries != null ) {
            timeSeriesColl.removeSeries( timeSeries ) ;
        }
    }
    
    @Override
    public void handleEvent( Event event ) {
        if( event.getEventType() == EVT_INDICATOR_DAY_VALUE ) {
            IndicatorDayValue val = ( IndicatorDayValue )event.getValue() ;
            if( val.getSymbol().equals( this.symbol ) ) {
                addValue( val.getIndicatorName(), val.getDate(), val.getValue() ) ;
            }
        }
    }
    
    public void addValue( String seriesKey, Date date, double value ) {
        TimeSeries series = timeSeriesMap.get( seriesKey ) ;
        if( series != null ) {
            series.add( new Day(date), value ) ;
        }
    }
}
