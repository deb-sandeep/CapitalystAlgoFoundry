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
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeries;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

import static com.sandy.capitalyst.algofoundry.AlgoFoundry.*;
import static com.sandy.capitalyst.algofoundry.EventCatalog.EVT_INDICATOR_DAY_VALUE;
import static com.sandy.capitalyst.algofoundry.core.ui.UITheme.*;

@Slf4j
public abstract class IndicatorChart extends JPanel
    implements EventSubscriber {
    
    private static final String PRIMARY_DATASET_NAME = "_PRIMARY_DATASET" ;
    
    protected JFreeChart        chart           = null ;
    protected ChartPanel        chartPanel      = null ;
    protected XYPlot            plot            = null ;
    protected TimeSeriesDataset primaryDataset  = null ;
    
    private final String symbol ;
    private final String title ;
    private final String yAxisLabel ;
    private final int    xAxisWindowSize ;
    
    private final List<String>                   datasetNames = new ArrayList<>() ;
    private final Map<String, TimeSeriesDataset> datasets     = new HashMap<>() ;
    private final Map<String, XYItemRenderer>    renderers    = new HashMap<>() ;
    
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
        
        primaryDataset = createPrimaryDataset() ;
        chart = createChart() ;
        plot = ( XYPlot )chart.getPlot() ;
        chartPanel = new ChartPanel( chart ) ;
        
        XYItemRenderer renderer = createPrimaryDatasetRenderer() ;
        if( renderer != null ) {
            plot.setRenderer( renderer ) ;
        }
        
        // The primary dataset and renderer are already added to the plot
        // so no need of calling the addDataset method.
        datasetNames.add( PRIMARY_DATASET_NAME ) ;
        datasets.put( PRIMARY_DATASET_NAME, primaryDataset ) ;
        renderers.put( PRIMARY_DATASET_NAME, plot.getRenderer() ) ;
        
        configureChart() ;
        configureTitle() ;
        configurePlot() ;
        configureXAxes() ;
        configureYAxes() ;
        configureChartPanel() ;
    }
    
    protected JFreeChart createChart() {
        return ChartFactory.createTimeSeriesChart(
                title,
                null,
                yAxisLabel,
                primaryDataset ) ;
    }
    
    protected TimeSeriesDataset createPrimaryDataset() {
        return new TimeSeriesDataset( PRIMARY_DATASET_NAME ) ;
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
        plot.setDomainGridlinePaint( Color.DARK_GRAY.darker() ) ;
        plot.setRangeGridlinePaint( Color.DARK_GRAY.darker() ) ;
        plot.setRangePannable( true ) ;
        plot.setDomainPannable( true ) ;
        
        AxisSpace space = new AxisSpace() ;
        space.setLeft( 50 ) ;
        plot.setFixedRangeAxisSpace( space ) ;
    }
    
    protected void configureXAxes() {
        
        DateAxis  xAxis = (DateAxis)plot.getDomainAxis() ;
        
        xAxis.setLabelFont( CHART_AXIS_LABEL_FONT ) ;
        xAxis.setTickLabelFont( CHART_AXIS_TICK_FONT ) ;
        xAxis.setTickLabelPaint( CHART_AXIS_TICK_COLOR ) ;
        xAxis.setLabelPaint( CHART_LABEL_COLOR ) ;
        
        xAxis.setDateFormatOverride( new SimpleDateFormat( "dd-MMM-yy" ) ) ;
    }
    
    protected void configureYAxes() {
        
        ValueAxis yAxis = plot.getRangeAxis() ;
        
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
    }
    
    protected TimeSeriesDataset addDataset( String name ) {
        return addDataset( name, null ) ;
    }
    
    protected TimeSeriesDataset addDataset( String name, XYItemRenderer renderer ) {
        
        TimeSeriesDataset dataset = new TimeSeriesDataset( name ) ;
        
        int plotIndex = datasetNames.indexOf( name ) + 1 ;
        if( renderer == null ) {
            renderer = new XYLineAndShapeRenderer( true, false);
        }
        renderers.put( name, renderer ) ;
        datasets.put( name, dataset ) ;
        
        plot.setRenderer( plotIndex, renderer ) ;
        plot.setDataset( plotIndex, dataset ) ;
        
        return dataset ;
    }
    
    protected TimeSeriesDataset getDataset( String name ) {
        return datasets.get( name ) ;
    }
    
    protected XYItemRenderer getRenderer( String name ) {
        return renderers.get( name ) ;
    }
    
    public void addIndicatorTimeSeries( String indicatorName ) {
        addIndicatorTimeSeries( this.primaryDataset, indicatorName ) ;
    }
    
    public void addIndicatorTimeSeries( TimeSeriesDataset dataset, String indicatorName ) {
        
        if( !dataset.containsIndicator( indicatorName ) ) {
            
            TimeSeries series = new TimeSeries( indicatorName ) ;
            XYItemRenderer renderer = renderers.get( dataset.getName() ) ;
            
            dataset.addSeries( series ) ;
            
            if( this.xAxisWindowSize > 0 ) {
                series.setMaximumItemCount( this.xAxisWindowSize ) ;
            }
            
            int seriesIndex = dataset.getSeriesCount()-1 ;
            renderer.setSeriesPaint( seriesIndex,
                                     IndicatorUtil.getColor( indicatorName ) ) ;
            renderer.setSeriesStroke( seriesIndex,
                                      IndicatorUtil.getStroke( indicatorName ) ) ;
        }
    }
    
    public void clearIndicatorTimeSeries( String indicatorName ) {
        clearIndicatorTimeSeries( primaryDataset, indicatorName ) ;
    }
    
    public void clearIndicatorTimeSeries( TimeSeriesDataset dataset, String indicatorName ) {
        dataset.getSeries( indicatorName ).clear() ;
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
    
    public void addValue( String indicatorName, Date date, double value ) {
        datasets.values().forEach( d -> d.addValue( indicatorName, date, value ) ) ;
    }
}
