package com.sandy.capitalyst.algofoundry.ui.panel.sim;

import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityHistEODAPIClient;
import com.sandy.capitalyst.algofoundry.ui.indchart.IndicatorChart;
import com.sandy.capitalyst.algofoundry.ui.indchart.MACDChart;
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
    private static final int MACD_CHART_HEIGHT = 150 ;
    
    private final EquityEODHistory history ;
    
    private final IndicatorChart priceChart ;
    private final IndicatorChart volumeChart ;
    private final IndicatorChart macdChart ;
    
    private final IndicatorChart[] charts ;
    
    private final SimControlPanel controlPanel ;
    
    private int curBarSeriesIndex = 0 ;
    
    public SimPanel( String symbol ) throws Exception {
        this.history = new EquityHistEODAPIClient().getEquityEODHistory( symbol ) ;
        
        this.priceChart  = new PriceChart( symbol ) ;
        this.volumeChart = new VolumeChart( symbol ) ;
        this.macdChart   = new MACDChart( symbol ) ;
        
        this.charts = new IndicatorChart[]{
                priceChart,
                volumeChart,
                macdChart
        } ;
        
        this.controlPanel = new SimControlPanel( this ) ;
        
        setUpUI() ;
    }
    
    private void setUpUI() {
        
        volumeChart.setPreferredSize( new Dimension( 100, VOL_CHART_HEIGHT ) ) ;
        macdChart.setPreferredSize( new Dimension( 100, MACD_CHART_HEIGHT ) ) ;
        
        priceChart.hideXAxis() ;
        volumeChart.hideXAxis() ;

        initPanelUI( this ) ;
        add( getChartPanel(), BorderLayout.CENTER ) ;
        add( controlPanel, BorderLayout.EAST ) ;
    }
    
    private JPanel getChartPanel() {
        JPanel panel = getNewJPanel() ;
        panel.add( priceChart, BorderLayout.CENTER ) ;
        panel.add( getFooterChartPanel(), BorderLayout.SOUTH ) ;
        return panel ;
    }
    
    private JPanel getFooterChartPanel() {
        JPanel panel = getNewJPanel() ;
        panel.add( this.volumeChart, BorderLayout.NORTH ) ;
        panel.add( this.macdChart, BorderLayout.CENTER ) ;
        return panel ;
    }
    
    public boolean playCurrentBarSeriesData() {
        
        Set<EquityEODHistory.PayloadType> payloadTypes = new HashSet<>() ;
        for( IndicatorChart chart : charts ) {
            payloadTypes.addAll( chart.getConsumedPayloadTypes() ) ;
        }
        
        if( curBarSeriesIndex < history.getBarCount() ) {
            history.emitDayValues( curBarSeriesIndex, payloadTypes ) ;
            this.curBarSeriesIndex++ ;
            return true ;
        }
        return false ;
    }
    
    public void restartSimulation() {
        this.curBarSeriesIndex = 0 ;
        for( IndicatorChart chart : charts ) {
            chart.clearChart() ;
        }
    }
}
