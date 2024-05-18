package com.sandy.capitalyst.algofoundry.ui.indchart;

import com.sandy.capitalyst.algofoundry.equityhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.equityhistory.dayvalue.OHLCVDayValue;
import com.sandy.capitalyst.algofoundry.strategy.AbstractZonedTradeStrategy;
import com.sandy.capitalyst.algofoundry.strategy.StrategyZoneListener;
import com.sandy.capitalyst.algofoundry.ui.indchart.util.CircleAnnotationDrawable;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory.PayloadType;

@Slf4j
public class VolumeChart extends IndicatorChart
        implements StrategyZoneListener {
    
    private static final Color BLACKOUT_COLOR     = new Color( 58, 57, 57 ) ;
    private static final Color LOOKOUT_COLOR      = new Color( 96, 96, 96 ) ;
    private static final Color ENTRY_ACTIVE_COLOR = new Color( 81, 134, 62 ) ;
    private static final Color EXIT_ACTIVE_COLOR  = new Color( 131, 52, 52 ) ;
    
    private TimeSeries dayVolumeTimeSeries ;
    
    public VolumeChart( String symbol ) {
        super( symbol, "Volume" ) ;
        postInitializeChart() ;
    }
    
    protected void postInitializeChart() {
        consumedPayloadTypes.add( PayloadType.OHLCV ) ;
        attachVolumeTimeSeries() ;
    }
    
    @Override
    public void clearChart() {
        dayVolumeTimeSeries.clear() ;
    }
    
    @Override
    public List<PayloadType> getConsumedPayloadTypes() {
        return this.consumedPayloadTypes ;
    }

    @Override
    protected void consumeDayValue( AbstractDayValue payload ) {
        Day day = new Day( payload.getDate() ) ;
        if( payload instanceof OHLCVDayValue ohlcv ) {
            double vol = ohlcv.getVolume() ;
            vol = vol < 1 ? 0 : vol/1000 ;
            dayVolumeTimeSeries.add( day, vol ) ;
        }
    }
    
    private void attachVolumeTimeSeries() {
        
        dayVolumeTimeSeries = new TimeSeries( "Day Vol" ) ;
        super.configureSeries( dayVolumeTimeSeries ) ;
        
        primaryDataset.addSeries( dayVolumeTimeSeries ) ;
        
        XYBarRenderer renderer = new XYBarRenderer() ;
        renderer.setBarPainter( new StandardXYBarPainter() ) ;
        renderer.setShadowVisible( false ) ;
        renderer.setSeriesPaint( 0, Color.DARK_GRAY );
        plot.setRenderer( renderer ) ;
    }
    
    @Override
    public void handleZone( Date date, AbstractZonedTradeStrategy.Zone zone,
                            double volume ) {
        
        Color color = BLACKOUT_COLOR ;
        switch( zone ) {
            case LOOKOUT      -> color = LOOKOUT_COLOR ;
            case ENTRY_ACTIVE -> color = ENTRY_ACTIVE_COLOR ;
            case EXIT_ACTIVE  -> color = EXIT_ACTIVE_COLOR ;
        }
        
        double yVal = volume/1000 ;
        
        CircleAnnotationDrawable cd = new CircleAnnotationDrawable( color ) ;
        XYAnnotation annotation = new XYDrawableAnnotation(
                                            (double)(date.getTime() + 86400*500),
                                            yVal,
                                            10, 10, cd ) ;
        SwingUtilities.invokeLater( () ->
                plot.getRenderer().addAnnotation( annotation ) ) ;
    }
}
