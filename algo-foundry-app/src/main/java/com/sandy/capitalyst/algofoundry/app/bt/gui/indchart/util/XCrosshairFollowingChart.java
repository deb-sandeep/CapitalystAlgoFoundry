package com.sandy.capitalyst.algofoundry.app.bt.gui.indchart.util;

import com.sandy.capitalyst.algofoundry.app.bt.gui.indchart.IndicatorChart;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;

public abstract class XCrosshairFollowingChart extends IndicatorChart
    implements CrossHairMoveListener {
    
    private Crosshair xCrosshair ;
    
    private final CrosshairOverlay crosshairOverlay = new CrosshairOverlay() ;
    private boolean crosshairEnabled = false ;

    protected XCrosshairFollowingChart( String symbol, String yLabel ) {
        super( symbol, yLabel ) ;
        attachCrosshair() ;
    }
    
    private void attachCrosshair() {
        
        xCrosshair = createGenericCrosshair() ;
        xCrosshair.setLabelVisible( false ) ;
        crosshairOverlay.addDomainCrosshair( xCrosshair ) ;
    }
    
    public void xCrosshairMoved( double x ) {
        if( x < 0 ) {
            if( crosshairEnabled ) {
                chartPanel.removeOverlay( crosshairOverlay ) ;
                crosshairEnabled = false ;
            }
        }
        else {
            if( !crosshairEnabled ) {
                chartPanel.addOverlay( crosshairOverlay ) ;
                crosshairEnabled = true ;
            }
        }
        
        if( crosshairEnabled ) {
            xCrosshair.setValue( x ) ;
        }
    }
}
