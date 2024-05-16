package com.sandy.capitalyst.algofoundry.ui.indchart;

import com.sandy.capitalyst.algofoundry.equityhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.equityhistory.dayvalue.BollingerBandDayValue;
import com.sandy.capitalyst.algofoundry.equityhistory.dayvalue.OHLCVDayValue;
import com.sandy.capitalyst.algofoundry.core.ui.UITheme;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.YIntervalDataItem;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

import java.awt.*;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory.PayloadType;

public class PriceChart extends IndicatorChart {
    
    private static final Color CLOSING_PRICE_COLOR = new Color( 178, 255, 102 ).darker() ;
    
    private TimeSeries closePriceTimeSeries ;
    private YIntervalSeries bollingerBandsSeries ;
    
    public PriceChart( String symbol ) {
        super( symbol, "Price" ) ;
        postInitializeChart() ;
    }
    
    protected void postInitializeChart() {
        consumedPayloadTypes.add( PayloadType.OHLCV ) ;
        consumedPayloadTypes.add( PayloadType.BOLLINGER ) ;

        attachClosePriceTimeSeries() ;
        attachBollingerBands() ;
    }
    
    @Override
    public List<PayloadType> getConsumedPayloadTypes() {
        return this.consumedPayloadTypes ;
    }
    
    @Override
    public void clearChart() {
        closePriceTimeSeries.clear() ;
        bollingerBandsSeries.clear() ;
    }
    
    @Override
    protected void consumeDayValue( AbstractDayValue payload ) {
        Day day = new Day( payload.getDate() ) ;
        if( payload instanceof OHLCVDayValue ohlcv ) {
            closePriceTimeSeries.add( day, ohlcv.getClose()  ) ;
        }
        else if( payload instanceof BollingerBandDayValue b ) {
            YIntervalDataItem data = new YIntervalDataItem(
                    b.getDate().getTime(),
                    b.getMid(),
                    b.getLow(),
                    b.getHigh() ) ;
            bollingerBandsSeries.add( data, true ) ;
        }
    }
    
    // Close Price time series is attached as the primary time series.
    private void attachClosePriceTimeSeries() {
        
        closePriceTimeSeries = new TimeSeries( "Day Close" ) ;
        super.configureSeries( closePriceTimeSeries ) ;
        
        primaryDataset.addSeries( closePriceTimeSeries ) ;
        
        XYItemRenderer renderer = plot.getRenderer() ;
        renderer.setSeriesPaint( primaryDataset.getSeriesCount()-1,
                                 CLOSING_PRICE_COLOR ) ;
        renderer.setSeriesStroke( 0, UITheme.LINE_STROKE_0_5 );
    }
    
    private void attachBollingerBands() {
        
        bollingerBandsSeries = new YIntervalSeries( "Bollinger Bands" ) ;
        super.configureSeries( bollingerBandsSeries ) ;
        
        YIntervalSeriesCollection dataset = new YIntervalSeriesCollection() ;
        dataset.addSeries( bollingerBandsSeries ) ;
        
        plot.setDataset( 1, dataset ) ;
        
        DeviationRenderer renderer = new DeviationRenderer( true, false ) ;
        renderer.setAlpha( 0.35f ) ;
        renderer.setSeriesStroke( 0, UITheme.DASHED_STROKE ) ;
        renderer.setSeriesFillPaint( 0, Color.DARK_GRAY ) ;
        renderer.setSeriesPaint( 0, Color.GRAY ) ;
        
        plot.setRenderer(1, renderer ) ;
    }
}
