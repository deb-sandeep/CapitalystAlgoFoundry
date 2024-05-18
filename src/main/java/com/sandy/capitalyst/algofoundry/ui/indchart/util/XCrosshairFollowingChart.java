package com.sandy.capitalyst.algofoundry.ui.indchart.util;

import com.sandy.capitalyst.algofoundry.ui.indchart.IndicatorChart;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;

import java.util.Date;

public abstract class XCrosshairFollowingChart extends IndicatorChart
    implements CrossHairMoveListener {
    
    private Crosshair xCrosshair ;

    protected XCrosshairFollowingChart( String symbol, String yLabel ) {
        super( symbol, yLabel ) ;
        attachCrosshair() ;
    }
    
    private void attachCrosshair() {
        
        CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
        
        xCrosshair = createGenericCrosshair() ;
        xCrosshair.setLabelVisible( false ) ;
        crosshairOverlay.addDomainCrosshair( xCrosshair ) ;
        
        chartPanel.addOverlay( crosshairOverlay ) ;
    }
    
    public void xCrosshairMoved( double x ) {
        xCrosshair.setValue( x ) ;
    }
}
