package com.sandy.capitalyst.algofoundry.app.backtester.gui.indchart;

import com.sandy.capitalyst.algofoundry.app.core.ui.UITheme;
import com.sandy.capitalyst.algofoundry.app.backtester.gui.indchart.util.XCrosshairFollowingChart;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.dayvalue.ADXDayValue;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.DayValue;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries.DayValueType;

@Slf4j
public class ADXChart extends XCrosshairFollowingChart {
    
    private static final Color POSITIVE_TREND_PAINT = new Color( 96, 145, 95, 61 ) ;
    private static final Color NEGATIVE_TREND_PAINT = new Color( 176, 82, 87, 61 ) ;
    
    private static final Color HIST_AREA_PAINT = new Color( 75, 75, 75, 174 ) ;
    
    private static final Color PLUS_DMI_PAINT  = new Color( 96, 145, 95, 100 ) ;
    private static final Color MINUS_DMI_PAINT = new Color( 176, 82, 87, 100 ) ;
    
    private static final Color ADX_25_PAINT = new Color( 255, 255, 255, 132 ) ;
    
    private TimeSeries adxTimeSeries ;
    private TimeSeries plusDMITimeSeries ;
    private TimeSeries minusDMITimeSeries ;
    private TimeSeries y25TimeSeries ;
    
    public ADXChart( String symbol ) {
        super( symbol, "ADX" ) ;
        postInitializeChart() ;
    }
    
    protected void postInitializeChart() {
        consumedDayValueTypes.add( DayValueType.ADX ) ;
        attachDITimeSeries() ;
        attachHistogramTimeSeries() ;
        attachY25TimeSeries() ;
    }
    
    private void attachDITimeSeries() {
        
        plusDMITimeSeries = new TimeSeries( "ADX +DMI" ) ;
        minusDMITimeSeries = new TimeSeries( "ADX -DMI" ) ;
        
        super.configureSeries( plusDMITimeSeries, minusDMITimeSeries ) ;
        
        primaryDataset.addSeries( plusDMITimeSeries ) ;
        primaryDataset.addSeries( minusDMITimeSeries ) ;
        
        XYDifferenceRenderer renderer ;
        renderer = new XYDifferenceRenderer( POSITIVE_TREND_PAINT,
                                             NEGATIVE_TREND_PAINT,
                                             false ) ;
        
        renderer.setSeriesPaint( 0, PLUS_DMI_PAINT ) ;
        renderer.setSeriesStroke( 0, UITheme.LINE_STROKE_1_5 ) ;
        
        renderer.setSeriesPaint( 1, MINUS_DMI_PAINT ) ;
        renderer.setSeriesStroke( 1, UITheme.LINE_STROKE_1_5 ) ;
        
        plot.setRenderer( renderer ) ;
    }
    
    private void attachHistogramTimeSeries() {
        
        TimeSeriesCollection dataset ;
        XYAreaRenderer renderer ;
        
        adxTimeSeries = new TimeSeries( "ADX" ) ;
        
        super.configureSeries( adxTimeSeries ) ;
        
        dataset = new TimeSeriesCollection() ;
        dataset.addSeries( adxTimeSeries ) ;
        
        renderer = new XYAreaRenderer() ;
        renderer.setSeriesPaint( 0, HIST_AREA_PAINT ) ;
        
        plot.setDataset( 1, dataset ) ;
        plot.setRenderer( 1, renderer ) ;
    }
    
    private void attachY25TimeSeries() {
        
        TimeSeriesCollection   dataset ;
        XYLineAndShapeRenderer renderer ;
        
        y25TimeSeries = new TimeSeries( "ADX 25" ) ;
        super.configureSeries( y25TimeSeries ) ;
        
        dataset = new TimeSeriesCollection() ;
        dataset.addSeries( y25TimeSeries ) ;
        
        renderer = new XYLineAndShapeRenderer( true, false ) ;
        renderer.setSeriesPaint( 0, ADX_25_PAINT ) ;
        renderer.setSeriesStroke( 0, UITheme.LINE_STROKE_1_0 ) ;
        
        plot.setDataset( 2, dataset ) ;
        plot.setRenderer( 2, renderer ) ;
    }
    
    @Override
    public List<DayValueType> getConsumedPayloadTypes() {
        return this.consumedDayValueTypes;
    }
    
    @Override
    public void clearChart() {
        super.clearSeries( adxTimeSeries,
                           plusDMITimeSeries,
                           minusDMITimeSeries,
                           y25TimeSeries ) ;
    }
    
    @Override
    protected void consumeDayValue( DayValue payload ) {
        Day day = new Day( payload.getDate() ) ;
        if( payload instanceof ADXDayValue adx ) {
            adxTimeSeries.add( day, adx.getAdx() ) ;
            plusDMITimeSeries.add( day, adx.getPlusDMI() ) ;
            minusDMITimeSeries.add( day, adx.getMinusDMI() ) ;
            y25TimeSeries.add( day, 25 ) ;
        }
    }
}
