package com.sandy.capitalyst.algofoundry.ui.panel.sim;

import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityHistEODAPIClient;
import com.sandy.capitalyst.algofoundry.ui.indchart.IndicatorChart;
import com.sandy.capitalyst.algofoundry.ui.indchart.PriceChart;
import com.sandy.capitalyst.algofoundry.ui.indchart.VolumeChart;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import static com.sandy.capitalyst.algofoundry.core.ui.SwingUtils.getNewJPanel;
import static com.sandy.capitalyst.algofoundry.core.ui.SwingUtils.initPanelUI;

@Slf4j
public class SimPanel extends JPanel {
    
    private static final int VOL_CHART_HEIGHT = 100 ;
    
    private final EquityEODHistory history ;
    
    private final IndicatorChart mainChart ;
    private final IndicatorChart volumeChart ;
    
    private final SimControlPanel controlPanel ;
    
    private int curBarSeriesIndex = 0 ;
    
    public SimPanel( String symbol ) throws Exception {
        this.history = new EquityHistEODAPIClient().getEquityEODHistory( symbol ) ;
        
        this.mainChart   = new PriceChart( symbol ) ;
        this.volumeChart = new VolumeChart( symbol ) ;
        
        this.controlPanel = new SimControlPanel( this ) ;
        
        setUpUI() ;
    }
    
    private void setUpUI() {
        
        volumeChart.setPreferredSize( new Dimension( 100, VOL_CHART_HEIGHT ) ) ;

        initPanelUI( this ) ;
        add( getChartPanel(), BorderLayout.CENTER ) ;
        add( controlPanel, BorderLayout.EAST ) ;
    }
    
    private JPanel getChartPanel() {
        JPanel panel = getNewJPanel() ;
        panel.add( mainChart, BorderLayout.CENTER ) ;
        panel.add( getFooterChartPanel(), BorderLayout.SOUTH ) ;
        return panel ;
    }
    
    private JPanel getFooterChartPanel() {
        JPanel panel = getNewJPanel() ;
        panel.setLayout( new GridLayout( 1, 1 ) ) ;
        panel.add( this.volumeChart ) ;
        return panel ;
    }
    
    public boolean playCurrentBarSeriesData() {
        
        Set<EquityEODHistory.PayloadType> payloadTypes = new HashSet<>() ;
        
        payloadTypes.addAll( mainChart.getConsumedPayloadTypes() ) ;
        payloadTypes.addAll( volumeChart.getConsumedPayloadTypes() ) ;
        
        if( curBarSeriesIndex < history.getBarCount() ) {
            history.emitDayValues( curBarSeriesIndex, payloadTypes ) ;
            this.curBarSeriesIndex++ ;
            return true ;
        }
        return false ;
    }
    
    public void restartSimulation() {
        this.curBarSeriesIndex = 0 ;
    }
}
