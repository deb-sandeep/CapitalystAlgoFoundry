package com.sandy.capitalyst.algofoundry.ui.indchart;

import com.sandy.capitalyst.algofoundry.apiclient.histeod.payload.AbstractDayValuePayload;
import com.sandy.capitalyst.algofoundry.apiclient.histeod.payload.MACDPayload;
import com.sandy.capitalyst.algofoundry.core.ui.UITheme;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityEODHistory.PayloadType;

@Slf4j
public class MACDChart extends IndicatorChart {
    
    private static final Color POSITIVE_PAINT = new Color( 96, 145, 95, 148 ) ;
    private static final Color NEGATIVE_PAINT = new Color( 176, 82, 87, 140 ) ;
    
    private TimeSeries macdTimeSeries ;
    private TimeSeries macdSignalTimeSeries ;
    private TimeSeries macdHistogramTimeSeries ;
    private TimeSeries y0TimeSeries ;
    
    public MACDChart( String symbol ) {
        super( symbol, "MACD" ) ;
        postInitializeChart() ;
    }
    
    protected void postInitializeChart() {
        consumedPayloadTypes.add( PayloadType.MACD ) ;
        attachDifferenceTimeSeries() ;
        attachHistogramTimeSeries() ;
    }
    
    private void attachDifferenceTimeSeries() {
        
        macdTimeSeries = new TimeSeries( "MACD" ) ;
        macdSignalTimeSeries = new TimeSeries( "MACD Signal" ) ;
        
        super.configureSeries( macdTimeSeries ) ;
        super.configureSeries( macdSignalTimeSeries ) ;
        
        primaryDataset.addSeries( macdTimeSeries ) ;
        primaryDataset.addSeries( macdSignalTimeSeries ) ;
        
        XYItemRenderer renderer = plot.getRenderer() ;
        
        renderer.setSeriesPaint( 0, POSITIVE_PAINT ) ;
        renderer.setSeriesPaint( 1, NEGATIVE_PAINT ) ;
        
        renderer.setSeriesStroke( 0, UITheme.LINE_STROKE_1_5 ) ;
        renderer.setSeriesStroke( 1, UITheme.LINE_STROKE_1_5 ) ;
    }
    
    private void attachHistogramTimeSeries() {
        
        TimeSeriesCollection dataset ;
        XYDifferenceRenderer renderer ;

        macdHistogramTimeSeries = new TimeSeries( "MACD Histogram" ) ;
        y0TimeSeries = new TimeSeries( "X Axis" ) ;
        
        super.configureSeries( macdHistogramTimeSeries ) ;
        super.configureSeries( y0TimeSeries ) ;
        
        dataset = new TimeSeriesCollection() ;
        dataset.addSeries( macdHistogramTimeSeries ) ;
        dataset.addSeries( y0TimeSeries ) ;
        
        renderer = new XYDifferenceRenderer( POSITIVE_PAINT, NEGATIVE_PAINT, false) ;
        renderer.setSeriesPaint( 0, POSITIVE_PAINT ) ;
        renderer.setSeriesPaint( 1, NEGATIVE_PAINT ) ;
        
        plot.setDataset( 1, dataset ) ;
        plot.setRenderer( 1, renderer ) ;
    }
    
    @Override
    public List<PayloadType> getConsumedPayloadTypes() {
        return this.consumedPayloadTypes ;
    }
    
    @Override
    public void clearChart() {
        macdTimeSeries.clear() ;
        macdSignalTimeSeries.clear() ;
        macdHistogramTimeSeries.clear() ;
        y0TimeSeries.clear() ;
    }
    
    @Override
    protected void handleDayValuePayload( AbstractDayValuePayload payload ) {
        Day day = new Day( payload.getDate() ) ;
        if( payload instanceof MACDPayload macd ) {
            macdTimeSeries.add( day, macd.getMacd() ) ;
            macdSignalTimeSeries.add( day, macd.getSignal() ) ;
            macdHistogramTimeSeries.add( day, macd.getHistogramValue() ) ;
            y0TimeSeries.add( day, 0 ) ;
        }
    }
}
