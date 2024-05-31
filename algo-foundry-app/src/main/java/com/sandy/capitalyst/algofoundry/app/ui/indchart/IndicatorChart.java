package com.sandy.capitalyst.algofoundry.app.ui.indchart;

import com.sandy.capitalyst.algofoundry.app.core.ui.UITheme;
import com.sandy.capitalyst.algofoundry.strategy.candleseries.DayValue;
import com.sandy.capitalyst.algofoundry.strategy.candleseries.DayValueListener;
import com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Crosshair;
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

import static com.sandy.capitalyst.algofoundry.app.AlgoFoundry.getConfig;

@Slf4j
public abstract class IndicatorChart extends JPanel
    implements DayValueListener {
    
    protected static final SimpleDateFormat CROSSHAIR_DATE_FMT  = new SimpleDateFormat( "dd-MMM-yyyy" ) ;

    protected final int    xAxisWindowSize ;
    protected final String symbol ;

    protected JFreeChart           chart          = null ;
    protected ChartPanel           chartPanel     = null ;
    protected XYPlot               plot           = null ;
    protected TimeSeriesCollection primaryDataset = null ;
    
    protected final List<CandleSeries.DayValueType> consumedDayValueTypes = new ArrayList<>() ;
    
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
        
        xAxis.setLabelFont( UITheme.CHART_AXIS_LABEL_FONT ) ;
        xAxis.setTickLabelFont( UITheme.CHART_AXIS_TICK_FONT ) ;
        xAxis.setTickLabelPaint( UITheme.CHART_AXIS_TICK_COLOR ) ;
        xAxis.setLabelPaint( UITheme.CHART_LABEL_COLOR ) ;
        xAxis.setLowerMargin( 0 ) ;
        xAxis.setUpperMargin( 0.01 ) ;
        
        xAxis.setDateFormatOverride( new SimpleDateFormat( "dd-MMM-yy" ) ) ;
    }
    
    protected void configureYAxes( ValueAxis yAxis ) {
        
        yAxis.setLabelFont( UITheme.CHART_AXIS_LABEL_FONT ) ;
        yAxis.setTickLabelFont( UITheme.CHART_AXIS_TICK_FONT ) ;
        yAxis.setTickLabelPaint( UITheme.CHART_AXIS_TICK_COLOR ) ;
        yAxis.setLabelPaint( UITheme.CHART_LABEL_COLOR ) ;
    }
    
    protected void configureChartPanel() {
        chartPanel.setDoubleBuffered( true ) ;
        chartPanel.setFillZoomRectangle( true ) ;
        chartPanel.setMouseWheelEnabled( true ) ;
        chartPanel.setMinimumDrawWidth( 0 ) ;
        chartPanel.setMaximumDrawWidth( Integer.MAX_VALUE ) ;
        chartPanel.setMinimumDrawHeight( 0 ) ;
        chartPanel.setMaximumDrawHeight( Integer.MAX_VALUE ) ;
        chartPanel.setBorder( BorderFactory.createLineBorder( UITheme.CHART_BORDER_COLOR, 1 ) );
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
    
    public abstract List<CandleSeries.DayValueType> getConsumedPayloadTypes() ;
    
    public abstract void clearChart() ;
    
    protected void clearSeries( TimeSeries... serieses ) {
        for( TimeSeries series : serieses ) {
            series.clear() ;
        }
    }
    
    public final void handleDayValue( DayValue dayValue ) {
        SwingUtilities.invokeLater( () -> {
            consumeDayValue( dayValue ) ;
        } );
    }
    
    protected abstract void consumeDayValue( DayValue dayValue ) ;
    
    protected Crosshair createGenericCrosshair() {
        
        Crosshair crosshair = new Crosshair( Double.NaN, Color.GRAY.darker(),
                new BasicStroke(0f));
        crosshair.setLabelVisible( true ) ;
        crosshair.setLabelBackgroundPaint( UITheme.BACKGROUND_COLOR.brighter() ) ;
        return crosshair ;
    }
}
