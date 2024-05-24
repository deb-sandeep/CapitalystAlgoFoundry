package com.sandy.capitalyst.algofoundry.ui.indchart;

import com.sandy.capitalyst.algofoundry.core.ui.UITheme;
import com.sandy.capitalyst.algofoundry.equityhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.equityhistory.dayvalue.BollingerBandDayValue;
import com.sandy.capitalyst.algofoundry.equityhistory.dayvalue.MADayValue;
import com.sandy.capitalyst.algofoundry.equityhistory.dayvalue.OHLCVDayValue;
import com.sandy.capitalyst.algofoundry.strategy.StrategyEvent;
import com.sandy.capitalyst.algofoundry.strategy.StrategyEventListener;
import com.sandy.capitalyst.algofoundry.strategy.event.TradeEvent;
import com.sandy.capitalyst.algofoundry.ui.indchart.util.CircleAnnotationDrawable;
import com.sandy.capitalyst.algofoundry.ui.indchart.util.CrossHairMoveListener;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.YIntervalDataItem;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.RectangleEdge;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory.PayloadType;

@Slf4j
public class PriceChart extends IndicatorChart
    implements StrategyEventListener {
    
    private static final DecimalFormat    CROSSHAIR_PRICE_FMT = new DecimalFormat( "###.0" ) ;
    
    private static final Color CLOSING_PRICE_COLOR = new Color( 178, 255, 102 ).darker() ;
    private static final Color EMA5_COLOR          = new Color( 121, 168, 252, 255 ) ;
    private static final Color BOLLINGER_MID_COLOR = new Color( 175, 65, 65, 255 ) ;
    private static final Color POS_EMA5_LABEL_COLOR= new Color( 43, 229, 7, 126 ) ;
    private static final Color NEG_EMA5_LABEL_COLOR= new Color( 175, 65, 65, 144 ) ;
    
    private TimeSeries ema5TimeSeries ;
    private TimeSeries closePriceTimeSeries ;
    private YIntervalSeries bollingerBandsSeries ;
    
    private Crosshair xCrosshair ;
    private Crosshair ema5Crosshair;
    private Crosshair ema20Crosshair;
    
    private final CrosshairOverlay crosshairOverlay = new CrosshairOverlay() ;
    private boolean crosshairEnabled = false ;
    
    private List<CrossHairMoveListener> crossHairMoveListeners = new ArrayList<>() ;
    
    public PriceChart( String symbol ) {
        super( symbol, "Price" ) ;
        postInitializeChart() ;
        attachCrosshair() ;
    }
    
    protected void postInitializeChart() {
        consumedPayloadTypes.add( PayloadType.OHLCV ) ;
        consumedPayloadTypes.add( PayloadType.BOLLINGER ) ;
        consumedPayloadTypes.add( PayloadType.EMA5 ) ;

        attachClosePriceTimeSeries() ;
        attachBollingerBands() ;
        attachEMA5TimeSeries() ;
    }
    
    private void attachCrosshair() {
        
        this.xCrosshair = createXCrosshair() ;
        this.ema20Crosshair = createEMACrosshair( null ) ;
        this.ema5Crosshair = createEMACrosshair( ema20Crosshair ) ;
        
        crosshairOverlay.addDomainCrosshair( xCrosshair ) ;
        crosshairOverlay.addRangeCrosshair( ema5Crosshair ) ;
        crosshairOverlay.addRangeCrosshair( ema20Crosshair ) ;
        
        chartPanel.addChartMouseListener( getCrosshairMouseListener() );
    }
    
    private Crosshair createXCrosshair() {
        Crosshair crosshair = createGenericCrosshair() ;
        crosshair.setLabelGenerator( c -> {
            Date date = new Date( (long)c.getValue() ) ;
            return CROSSHAIR_DATE_FMT.format( date ) ;
        } ) ;
        return crosshair ;
    }
    
    private Crosshair createEMACrosshair( Crosshair refCrosshair ) {
        Crosshair crosshair = createGenericCrosshair() ;
        crosshair.setLabelGenerator( c -> {
            if( refCrosshair == null ) {
                return CROSSHAIR_PRICE_FMT.format( c.getValue() );
            }
            else {
                double refVal = refCrosshair.getValue() ;
                double val = crosshair.getValue() ;
                double pct = ((val-refVal)/refVal)*100 ;
                
                Color labelBgColor = (pct>0)?POS_EMA5_LABEL_COLOR:NEG_EMA5_LABEL_COLOR ;
                ema5Crosshair.setLabelBackgroundPaint( labelBgColor ) ;

                return CROSSHAIR_PRICE_FMT.format( pct ) + " %" ;
            }
        } ) ;
        return crosshair ;
    }
    
    private ChartMouseListener getCrosshairMouseListener() {
        return new ChartMouseListener() {
            @Override
            public void chartMouseClicked( ChartMouseEvent event ) {}
            
            @Override
            public void chartMouseMoved( ChartMouseEvent event ) {
                
                if( !crosshairEnabled ) {
                    chartPanel.addOverlay( crosshairOverlay ) ;
                    crosshairEnabled = true ;
                }
                
                Rectangle2D dataArea = chartPanel.getScreenDataArea() ;
                ValueAxis   xAxis    = plot.getDomainAxis();
                double x = xAxis.java2DToValue( event.getTrigger().getX(),
                                                dataArea,
                                                RectangleEdge.BOTTOM);
                xCrosshair.setValue( x ) ;
                
                double ema5 = DatasetUtilities.findYValue( plot.getDataset(2), 0, x ) ;
                ema5Crosshair.setValue( ema5 ) ;
                
                double ema20 = DatasetUtilities.findYValue( plot.getDataset(1), 0, x ) ;
                ema20Crosshair.setValue( ema20 ) ;
                
                crossHairMoveListeners.forEach( l -> l.xCrosshairMoved( x ) ) ;
            }
        } ;
    }
    
    public void addCrosshairMoveListeners( CrossHairMoveListener... listeners ) {
        Collections.addAll( crossHairMoveListeners, listeners );
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
        
        chartPanel.removeOverlay( crosshairOverlay ) ;
        crossHairMoveListeners.forEach( l -> l.xCrosshairMoved( -1 ) ) ;
        crosshairEnabled = false ;
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
    public void handleStrategyEvent( StrategyEvent event ) {
        
        if( event instanceof TradeEvent te ) {
            
            Color color = ( te.getType() == TradeEvent.Type.BUY ) ?
                          Color.GREEN.darker() : Color.RED.darker() ;
            CircleAnnotationDrawable cd = new CircleAnnotationDrawable( color ) ;
            XYAnnotation annotation = new XYDrawableAnnotation(
                    (double)te.getDate().getTime(),
                    te.getClosingPrice(),
                    10, 10, cd ) ;
            
            SwingUtilities.invokeLater( () -> plot.getRenderer()
                    .addAnnotation( annotation ) ) ;
        }
    }
}
