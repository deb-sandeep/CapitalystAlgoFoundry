package com.sandy.capitalyst.algofoundry.ui.panel.sim;

import com.sandy.capitalyst.algofoundry.core.ui.IndicatorChart;

import javax.swing.*;

import java.awt.*;

import static com.sandy.capitalyst.algofoundry.core.ui.SwingUtils.* ;

public class FooterChartsPanel extends JPanel {
    
    private static final int CHART_HEIGHT = 150 ;
    
    private IndicatorChart macdChart = null ;
    private IndicatorChart rsiChart = null ;
    private IndicatorChart adxChart = null ;
    
    FooterChartsPanel( int xAxisWindowSize ) {
        this.macdChart = new IndicatorChart( null, "MACD", xAxisWindowSize ) ;
        this.rsiChart  = new IndicatorChart( null, "RSI",  xAxisWindowSize ) ;
        this.adxChart  = new IndicatorChart( null, "ADX",  xAxisWindowSize ) ;
        
        this.macdChart.setPreferredSize( new Dimension( 100, CHART_HEIGHT ) ) ;
        this.rsiChart.setPreferredSize( new Dimension( 100, CHART_HEIGHT ) ) ;
        this.adxChart.setPreferredSize( new Dimension( 100, CHART_HEIGHT ) ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        initPanelUI( this ) ;
        setLayout( new GridLayout( 3, 1 ) ) ;
        add( this.macdChart ) ;
        add( this.rsiChart ) ;
        add( this.adxChart ) ;
    }
}
