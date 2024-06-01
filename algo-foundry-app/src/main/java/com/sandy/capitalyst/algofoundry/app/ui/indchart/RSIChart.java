package com.sandy.capitalyst.algofoundry.app.ui.indchart;

import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.DayValue;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.dayvalue.RSIDayValue;
import com.sandy.capitalyst.algofoundry.app.core.ui.UITheme;
import com.sandy.capitalyst.algofoundry.app.ui.indchart.util.XCrosshairFollowingChart;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries.DayValueType;

@Slf4j
public class RSIChart extends XCrosshairFollowingChart {
    
    private static final Color OVERVALUED_PAINT  = new Color( 176, 82, 87, 140 ) ;
    
    private static final Color RSI_LINE_PAINT    = new Color( 124, 122, 122, 152 ) ;
    private static final Color RSI_CEILING_PAINT = new Color( 134, 73, 73, 181 ) ;
    private static final Color RSI_FLOOR_PAINT   = new Color( 95, 131, 83, 207 ) ;
    
    private static final int RSI_CEILING_VALUE = 70 ;
    private static final int RSI_FLOOR_VALUE = 30 ;
    
    private TimeSeries rsiTimeSeries ;
    private TimeSeries ceilingTimeSeries ;
    private TimeSeries floorTimeSeries ;
    
    public RSIChart( String symbol ) {
        super( symbol, "RSI" ) ;
        postInitializeChart() ;
    }
    
    protected void postInitializeChart() {
        consumedDayValueTypes.add( DayValueType.RSI ) ;
        attachDifferenceTimeSeries() ;
        attachCeilingCrossoverTimeSeries() ;
    }
    
    private void attachDifferenceTimeSeries() {
        
        rsiTimeSeries     = new TimeSeries( "RSI" ) ;
        ceilingTimeSeries = new TimeSeries( "RSI Ceiling" ) ;
        floorTimeSeries   = new TimeSeries( "RSI Floor" ) ;
        
        super.configureSeries( rsiTimeSeries, ceilingTimeSeries, floorTimeSeries ) ;
        
        primaryDataset.addSeries( rsiTimeSeries ) ;
        primaryDataset.addSeries( ceilingTimeSeries ) ;
        primaryDataset.addSeries( floorTimeSeries ) ;
        
        XYItemRenderer renderer = plot.getRenderer() ;
        renderer.setSeriesPaint( 0, RSI_LINE_PAINT ) ;
        renderer.setSeriesStroke( 0, UITheme.LINE_STROKE_1_0 ) ;
        
        renderer.setSeriesPaint( 1, RSI_CEILING_PAINT ) ;
        renderer.setSeriesStroke( 1, UITheme.LINE_STROKE_0_5 ) ;

        renderer.setSeriesPaint( 2, RSI_FLOOR_PAINT ) ;
        renderer.setSeriesStroke( 2, UITheme.LINE_STROKE_0_5 ) ;
    }
    
    private void attachCeilingCrossoverTimeSeries() {
        
        TimeSeriesCollection dataset ;
        XYDifferenceRenderer renderer ;
        
        dataset = new TimeSeriesCollection() ;
        dataset.addSeries( rsiTimeSeries ) ;
        dataset.addSeries( ceilingTimeSeries ) ;
        
        renderer = new XYDifferenceRenderer( OVERVALUED_PAINT, Color.BLACK, false ) ;
        renderer.setSeriesPaint( 0, RSI_LINE_PAINT ) ;
        renderer.setSeriesPaint( 1, RSI_CEILING_PAINT ) ;
        
        plot.setDataset( 1, dataset ) ;
        plot.setRenderer( 1, renderer ) ;
    }
    
    @Override
    public List<DayValueType> getConsumedPayloadTypes() {
        return this.consumedDayValueTypes;
    }
    
    @Override
    public void clearChart() {
        super.clearSeries( rsiTimeSeries, ceilingTimeSeries, floorTimeSeries ) ;
    }
    
    @Override
    protected void consumeDayValue( DayValue payload ) {
        Day day = new Day( payload.getDate() ) ;
        if( payload instanceof RSIDayValue rsi ) {
            rsiTimeSeries.add( day, rsi.getRsi() ) ;
            ceilingTimeSeries.add( day, RSI_CEILING_VALUE ) ;
            floorTimeSeries.add( day, RSI_FLOOR_VALUE ) ;
        }
    }
}
