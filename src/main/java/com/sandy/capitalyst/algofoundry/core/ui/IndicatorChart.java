package com.sandy.capitalyst.algofoundry.core.ui;

import org.jfree.chart.*;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.sandy.capitalyst.algofoundry.core.ui.UITheme.*;

public class IndicatorChart extends JPanel
    implements ChartMouseListener, ActionListener {
    
    private JFreeChart       chart      = null ;
    private ChartPanel       chartPanel = null ;
    private XYPlot           plot       = null ;
    private AbstractRenderer renderer   = null ;
    
    private JPopupMenu legendPopupMenu   = null ;
    private JMenuItem  removeSeriesMI    = null ;
    private JMenuItem  removeAllSeriesMI = null ;
    
    private String seriesMarkedForRemoval = null ;
    
    private final TimeSeriesCollection timeSeriesColl = new TimeSeriesCollection() ;
    private final Map<String, TimeSeries> timeSeriesMap = new HashMap<>() ;
    
    private final int windowSize ;
    
    public IndicatorChart( String title, int windowSize ) {
        this.windowSize = windowSize ;
        createChart( title ) ;
        setLayout( new BorderLayout() ) ;
        add( chartPanel, BorderLayout.CENTER ) ;
    }
    
    private void createChart( String title ) {
        chart = ChartFactory.createTimeSeriesChart(
                title,
                null,
                "Price",
                this.timeSeriesColl ) ;
        chart.setBackgroundPaint( Color.BLACK ) ;
        
        configureTitle( title ) ;
        configurePlot() ;
        configureAxes() ;
        configureLegends() ;
        configureLegendPopup() ;
        
        chartPanel = new ChartPanel( chart ) ;
        chartPanel.setDoubleBuffered( true ) ;
        chartPanel.setFillZoomRectangle( true ) ;
        chartPanel.setMouseWheelEnabled( true ) ;
    }
    
    private void configureTitle( String title ) {
        chart.setTitle( title ) ;
        chart.getTitle().setPaint( UITheme.CHART_TITLE_COLOR ) ;
        chart.getTitle().setFont( UITheme.CHART_TITLE_FONT ) ;
    }
    
    private void configurePlot() {
        
        plot = ( XYPlot )chart.getPlot() ;
        plot.setBackgroundPaint( Color.BLACK ) ;
        plot.setDomainGridlinePaint( Color.DARK_GRAY.darker() ) ;
        plot.setRangeGridlinePaint( Color.DARK_GRAY.darker() ) ;
        plot.setRangePannable( true ) ;
        plot.setDomainPannable( true ) ;
        
        renderer = ( AbstractRenderer )plot.getRenderer() ;
    }
    
    private void configureAxes() {
        
        ValueAxis yAxis = plot.getRangeAxis() ;
        DateAxis  xAxis = (DateAxis)plot.getDomainAxis() ;
        
        yAxis.setLabelFont( CHART_AXIS_FONT ) ;
        xAxis.setLabelFont( CHART_AXIS_FONT ) ;
        
        yAxis.setTickLabelFont( CHART_AXIS_FONT ) ;
        xAxis.setTickLabelFont( CHART_AXIS_FONT ) ;
        
        yAxis.setTickLabelPaint( CHART_AXIS_TICK_COLOR ) ;
        xAxis.setTickLabelPaint( CHART_AXIS_TICK_COLOR ) ;
        
        xAxis.setDateFormatOverride(new SimpleDateFormat("dd-MMM-yy"));
    }
    
    private void configureLegends() {
        
        LegendTitle legend = chart.getLegend() ;
        legend.setItemFont( CHART_LEGEND_FONT ) ;
        legend.setItemPaint( Color.LIGHT_GRAY.darker() ) ;
        legend.setBackgroundPaint( Color.BLACK );
    }
    
    private void configureLegendPopup() {
        
        removeSeriesMI = new JMenuItem( "Remove legend" ) ;
        removeSeriesMI.addActionListener( this )  ;
        
        removeAllSeriesMI = new JMenuItem( "Remove all legends" ) ;
        removeAllSeriesMI.addActionListener( this ) ;
        
        legendPopupMenu = new JPopupMenu() ;
        legendPopupMenu.add( removeSeriesMI ) ;
        legendPopupMenu.add( removeAllSeriesMI ) ;
    }
    
    public void addSeries( TimeSeries series ) {
        
        if( !timeSeriesMap.containsKey( (String)series.getKey() ) ) {
            
            String seriesKey = ( String ) series.getKey() ;
            timeSeriesMap.put( seriesKey, series ) ;
            timeSeriesColl.addSeries( series ) ;
            
            if( this.windowSize > 0 ) {
                series.setMaximumItemCount( this.windowSize ) ;
            }
            
            int seriesIndex = timeSeriesColl.getSeriesCount()-1 ;
            renderer.setSeriesPaint( seriesIndex,
                                     SeriesRenderingAttributes.getColor( seriesKey ) ) ;
            renderer.setSeriesStroke( seriesIndex,
                                      SeriesRenderingAttributes.getStroke( seriesKey ) ) ;
        }
    }
    
    public TimeSeries getSeries( String key ) {
        return timeSeriesMap.get( key ) ;
    }
    
    public void removeSeries( TimeSeries series ) {
        timeSeriesMap.remove( series.getKey() ) ;
        timeSeriesColl.removeSeries( series ) ;
    }
    
    public void removeSeries( String key ) {
        TimeSeries timeSeries = timeSeriesMap.remove( key ) ;
        if( timeSeries != null ) {
            timeSeriesColl.removeSeries( timeSeries ) ;
        }
    }
    
    public void removeAllSeries() {
        Collection<String> seriesNames = new ArrayList<>( timeSeriesMap.keySet() );
        for( String key : seriesNames ) {
            removeSeries( key ) ;
        }
    }
    
    public void addValue( String seriesKey, Date date, double value ) {
        TimeSeries series = timeSeriesMap.get( seriesKey ) ;
        if( series != null ) {
            series.add( new Day(date), value ) ;
        }
    }
    
    @Override
    public void chartMouseClicked( ChartMouseEvent event ) {
        ChartEntity entity = event.getEntity() ;
        MouseEvent  mouse  = event.getTrigger() ;
        
        if( entity instanceof LegendItemEntity legend ) {
            
            String seriesKey = ( String )legend.getSeriesKey() ;
            if( SwingUtilities.isRightMouseButton( mouse ) &&
                mouse.isControlDown() ) {
                seriesMarkedForRemoval = seriesKey ;
                legendPopupMenu.show( this, mouse.getX(), mouse.getY() );
            }
        }
    }
    
    @Override
    public void chartMouseMoved( ChartMouseEvent event ) { /* NOOP */ }
    
    @Override
    public void actionPerformed( ActionEvent e ) {
        JMenuItem menu = ( JMenuItem )e.getSource() ;
        if( menu == removeSeriesMI ) {
            removeSeries( seriesMarkedForRemoval ) ;
        }
        else if( menu == removeAllSeriesMI ) {
            removeAllSeries() ;
        }
    }
}
