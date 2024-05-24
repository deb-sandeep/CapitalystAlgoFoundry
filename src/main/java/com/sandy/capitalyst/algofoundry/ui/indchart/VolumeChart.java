package com.sandy.capitalyst.algofoundry.ui.indchart;

import com.sandy.capitalyst.algofoundry.eodhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.eodhistory.dayvalue.OHLCVDayValue;
import com.sandy.capitalyst.algofoundry.strategy.signal.SignalStrategyEvent;
import com.sandy.capitalyst.algofoundry.strategy.signal.SignalStrategyEventListener;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.CurrentSignalZoneEvent;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.annotations.XYBoxAnnotation;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.eodhistory.EquityEODHistory.PayloadType;

@Slf4j
public class VolumeChart extends IndicatorChart
        implements SignalStrategyEventListener {
    
    private static final Color BLACKOUT_COLOR     = new Color( 58, 57, 57 ) ;
    private static final Color LOOKOUT_COLOR      = new Color( 96, 96, 96 ) ;
    private static final Color ENTRY_ACTIVE_COLOR = new Color( 81, 134, 62, 100 ) ;
    private static final Color EXIT_ACTIVE_COLOR  = new Color( 131, 52, 52, 137 ) ;
    
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
    public void handleStrategyEvent( SignalStrategyEvent event ) {
        if( event instanceof CurrentSignalZoneEvent ze ) {
            Color color = BLACKOUT_COLOR ;
            switch( ze.getZoneType() ) {
                case LOOKOUT -> color = LOOKOUT_COLOR ;
                case BUY     -> color = ENTRY_ACTIVE_COLOR ;
                case SELL    -> color = EXIT_ACTIVE_COLOR ;
            }
            
            double yVal = ze.getBar().getVolume().doubleValue()/1000 ;
            
            XYBoxAnnotation annotation = new XYBoxAnnotation(
                    (double)(ze.getDate().getTime()),
                    0,
                    (double)(ze.getDate().getTime() + 86400*1000),
                    yVal,
                    null, null, color
            ) ;
            SwingUtilities.invokeLater( () ->
                    plot.getRenderer().addAnnotation( annotation ) ) ;
        }
    }
}
