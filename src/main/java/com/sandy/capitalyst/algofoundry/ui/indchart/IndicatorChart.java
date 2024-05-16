package com.sandy.capitalyst.algofoundry.ui.indchart;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.equityhistory.AbstractDayValuePayload;
import com.sandy.capitalyst.algofoundry.core.bus.Event;
import com.sandy.capitalyst.algofoundry.core.bus.EventSubscriber;
import com.sandy.capitalyst.algofoundry.core.ui.UITheme;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.ComparableObjectSeries;
import org.jfree.data.general.Series;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.AlgoFoundry.*;
import static com.sandy.capitalyst.algofoundry.EventCatalog.EVT_INDICATOR_DAY_VALUE;
import static com.sandy.capitalyst.algofoundry.core.ui.UITheme.*;

@Slf4j
public abstract class IndicatorChart extends JPanel
    implements EventSubscriber {
    
    protected final int    xAxisWindowSize ;
    protected final String symbol ;

    protected JFreeChart           chart          = null ;
    protected ChartPanel           chartPanel     = null ;
    protected XYPlot               plot           = null ;
    protected TimeSeriesCollection primaryDataset = null ;
    
    protected final List<EquityEODHistory.PayloadType> consumedPayloadTypes = new ArrayList<>() ;
    
    private final String title ;
    private final String yAxisLabel ;
    
    protected IndicatorChart( String symbol, String yLabel ) {
        this( symbol, null, yLabel ) ;
    }
    
    protected IndicatorChart( String symbol, String title, String yLabel ) {
        
        this.symbol = symbol ;
        this.title = title ;
        this.yAxisLabel = yLabel ;
        this.xAxisWindowSize = getConfig().getDateWindowSize() ;
        
        initializeChart() ;
        
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
    
    private void initializeChart() {
        
        primaryDataset = new TimeSeriesCollection() ;
        chart = createChart() ;
        plot = ( XYPlot )chart.getPlot() ;
        chartPanel = new ChartPanel( chart ) ;
        
        XYItemRenderer renderer = createPrimaryDatasetRenderer() ;
        if( renderer != null ) {
            plot.setRenderer( renderer ) ;
        }
        
        configureChart() ;
        configureTitle() ;
        configurePlot() ;
        
        DateAxis  xAxis = (DateAxis)plot.getDomainAxis() ;
        ValueAxis yAxis = plot.getRangeAxis() ;
        
        configureXAxes( xAxis ) ;
        configureYAxes( yAxis ) ;
        configureChartPanel() ;
    }
    
    protected JFreeChart createChart() {
        return ChartFactory.createTimeSeriesChart(
                title,
                null,
                yAxisLabel,
                primaryDataset ) ;
    }
    
    protected XYItemRenderer createPrimaryDatasetRenderer() {
        // By default, the renderer that comes with TimeSeriesChart is used.
        return null ;
    }
    
    protected void configureChart() {
        chart.setBackgroundPaint( Color.BLACK ) ;
        chart.removeLegend() ;
    }
    
    protected void configureTitle() {
        if( title != null ) {
            chart.setTitle( title ) ;
            chart.getTitle().setPaint( UITheme.CHART_TITLE_COLOR ) ;
            chart.getTitle().setFont( UITheme.CHART_TITLE_FONT ) ;
        }
    }
    
    protected void configurePlot() {
        
        plot.setBackgroundPaint( Color.BLACK ) ;
        plot.setDomainGridlinePaint( Color.DARK_GRAY ) ;
        plot.setRangeGridlinePaint( Color.DARK_GRAY ) ;
        plot.setRangePannable( true ) ;
        plot.setDomainPannable( true ) ;
        plot.setInsets( new RectangleInsets( 0, 0, 0, 0 ) );
        
        AxisSpace space = new AxisSpace() ;
        space.setLeft( 70 ) ;
        plot.setFixedRangeAxisSpace( space ) ;
    }
    
    protected void configureXAxes( DateAxis xAxis ) {
        
        xAxis.setLabelFont( CHART_AXIS_LABEL_FONT ) ;
        xAxis.setTickLabelFont( CHART_AXIS_TICK_FONT ) ;
        xAxis.setTickLabelPaint( CHART_AXIS_TICK_COLOR ) ;
        xAxis.setLabelPaint( CHART_LABEL_COLOR ) ;
        xAxis.setLowerMargin( 0 ) ;
        xAxis.setUpperMargin( 0.01 ) ;
        
        xAxis.setDateFormatOverride( new SimpleDateFormat( "dd-MMM-yy" ) ) ;
    }
    
    protected void configureYAxes( ValueAxis yAxis ) {
        
        yAxis.setLabelFont( CHART_AXIS_LABEL_FONT ) ;
        yAxis.setTickLabelFont( CHART_AXIS_TICK_FONT ) ;
        yAxis.setTickLabelPaint( CHART_AXIS_TICK_COLOR ) ;
        yAxis.setLabelPaint( CHART_LABEL_COLOR ) ;
    }
    
    protected void configureChartPanel() {
        chartPanel.setDoubleBuffered( true ) ;
        chartPanel.setFillZoomRectangle( true ) ;
        chartPanel.setMouseWheelEnabled( true ) ;
        chartPanel.setMinimumDrawWidth( 0 ) ;
        chartPanel.setMaximumDrawWidth( Integer.MAX_VALUE ) ;
        chartPanel.setMinimumDrawHeight( 0 ) ;
        chartPanel.setMaximumDrawHeight( Integer.MAX_VALUE ) ;
        chartPanel.setBorder( BorderFactory.createLineBorder( CHART_BORDER_COLOR, 1 ) );
    }
    
    protected void configureSeries( Series... seriesList ) {
        for( Series series : seriesList ) {
            if( xAxisWindowSize > 0 ) {
                if( series instanceof TimeSeries ts ) {
                    ts.setMaximumItemCount( xAxisWindowSize ) ;
                }
                else if( series instanceof ComparableObjectSeries cos ) {
                    cos.setMaximumItemCount( xAxisWindowSize ) ;
                }
            }
        }
    }
    
    public void hideXAxis() {
        DateAxis  xAxis = (DateAxis)plot.getDomainAxis() ;
        xAxis.setAxisLineVisible( false ) ;
        xAxis.setTickLabelsVisible( false ) ;
    }
    
    @Override
    public final void handleEvent( Event event ) {
        AbstractDayValuePayload payload ;
        if( event.getEventType() == EVT_INDICATOR_DAY_VALUE ) {
            payload = ( AbstractDayValuePayload )event.getValue() ;
            handleDayValuePayload( payload ) ;
        }
    }
    
    protected abstract void handleDayValuePayload( AbstractDayValuePayload payload ) ;
    
    public abstract List<EquityEODHistory.PayloadType> getConsumedPayloadTypes() ;
    
    public abstract void clearChart() ;
    
    protected void clearSeries( TimeSeries... serieses ) {
        for( TimeSeries series : serieses ) {
            series.clear() ;
        }
    }
}
