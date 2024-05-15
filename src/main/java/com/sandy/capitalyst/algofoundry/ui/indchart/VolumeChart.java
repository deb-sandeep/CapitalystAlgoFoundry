package com.sandy.capitalyst.algofoundry.ui.indchart;

import com.sandy.capitalyst.algofoundry.apiclient.histeod.payload.AbstractDayValuePayload;
import com.sandy.capitalyst.algofoundry.apiclient.histeod.payload.BollingerPayload;
import com.sandy.capitalyst.algofoundry.apiclient.histeod.payload.OHLCVPayload;
import com.sandy.capitalyst.algofoundry.core.ui.UITheme;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.YIntervalDataItem;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

import java.awt.*;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityEODHistory.PayloadType;

@Slf4j
public class VolumeChart extends IndicatorChart {
    
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
    protected void handleDayValuePayload( AbstractDayValuePayload payload ) {
        Day day = new Day( payload.getDate() ) ;
        if( payload instanceof OHLCVPayload ohlcv ) {
            double vol = ohlcv.getVolume() ;
            vol = vol < 1 ? 0 : (double)(vol/1000) ;
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
}
