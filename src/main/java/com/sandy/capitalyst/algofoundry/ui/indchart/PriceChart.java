package com.sandy.capitalyst.algofoundry.ui.indchart;

import com.sandy.capitalyst.algofoundry.core.ui.UITheme;
import com.sandy.capitalyst.algofoundry.equityhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.equityhistory.dayvalue.BollingerBandDayValue;
import com.sandy.capitalyst.algofoundry.equityhistory.dayvalue.MADayValue;
import com.sandy.capitalyst.algofoundry.equityhistory.dayvalue.OHLCVDayValue;
import com.sandy.capitalyst.algofoundry.strategy.TradeSignal;
import com.sandy.capitalyst.algofoundry.strategy.TradeSignalListener;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.YIntervalDataItem;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory.PayloadType;

@Slf4j
public class PriceChart extends IndicatorChart
    implements TradeSignalListener {
    
    private static final Color CLOSING_PRICE_COLOR = new Color( 178, 255, 102 ).darker() ;
    private static final Color EMA5_COLOR          = new Color( 121, 168, 252, 255 ) ;
    private static final Color BOLLINGER_MID_COLOR = new Color( 175, 65, 65, 255 ) ;
    
    private TimeSeries ema5TimeSeries ;
    private TimeSeries closePriceTimeSeries ;
    private YIntervalSeries bollingerBandsSeries ;
    
    public PriceChart( String symbol ) {
        super( symbol, "Price" ) ;
        postInitializeChart() ;
    }
    
    protected void postInitializeChart() {
        consumedPayloadTypes.add( PayloadType.OHLCV ) ;
        consumedPayloadTypes.add( PayloadType.BOLLINGER ) ;
        consumedPayloadTypes.add( PayloadType.EMA5 ) ;

        attachClosePriceTimeSeries() ;
        attachBollingerBands() ;
        attachEMA5TimeSeries() ;
    }
    
    @Override
    public List<PayloadType> getConsumedPayloadTypes() {
        return this.consumedPayloadTypes ;
    }
    
    @Override
    public void clearChart() {
        closePriceTimeSeries.clear() ;
        bollingerBandsSeries.clear() ;
        ema5TimeSeries.clear() ;
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
        else if( payload instanceof MADayValue ema5 ) {
            ema5TimeSeries.add( day, ema5.getValue() ) ;
        }
    }
    
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
        renderer.setSeriesPaint( 0, BOLLINGER_MID_COLOR ) ;
        
        plot.setRenderer(1, renderer ) ;
    }
    
    private void attachEMA5TimeSeries() {
        
        TimeSeriesCollection dataset ;
        XYItemRenderer renderer ;
        
        ema5TimeSeries = new TimeSeries( "EMA5" ) ;
        super.configureSeries( ema5TimeSeries ) ;
        
        dataset = new TimeSeriesCollection() ;
        dataset.addSeries( ema5TimeSeries ) ;
        
        renderer = new XYLineAndShapeRenderer( true, false ) ;
        renderer.setSeriesPaint( 0, EMA5_COLOR ) ;
        renderer.setSeriesStroke( 0, UITheme.DASHED_STROKE ) ;
        
        plot.setDataset( 2, dataset ) ;
        plot.setRenderer( 2, renderer ) ;
    }
    
    @Override
    public void handleTradeSignal( TradeSignal signal ) {
        
        Color color = ( signal.getType() == TradeSignal.Type.ENTRY ) ?
                      Color.GREEN.darker() : Color.RED.darker() ;
        CircleAnnotationDrawable cd = new CircleAnnotationDrawable( color ) ;
        XYAnnotation annotation = new XYDrawableAnnotation(
                                            (double)signal.getDate().getTime(),
                                            signal.getPrice(),
                                            10, 10, cd ) ;
        
        SwingUtilities.invokeLater( () -> {
            plot.getRenderer().addAnnotation( annotation ) ;
        } );
    }
}
