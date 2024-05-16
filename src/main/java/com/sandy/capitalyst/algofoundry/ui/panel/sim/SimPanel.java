package com.sandy.capitalyst.algofoundry.ui.panel.sim;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityHistEODAPIClient;
import com.sandy.capitalyst.algofoundry.ui.indchart.*;
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
    private static final int RSI_CHART_HEIGHT = 150 ;
    private static final int ADX_CHART_HEIGHT = 150 ;
    
    private final EquityEODHistory history ;
    
    private final IndicatorChart priceChart ;
    private final IndicatorChart volumeChart ;
    private final IndicatorChart macdChart ;
    private final IndicatorChart rsiChart ;
    private final IndicatorChart adxChart ;
    
    private final IndicatorChart[] charts ;
    
    private final SimControlPanel controlPanel ;
    
    private int curBarSeriesIndex = 0 ;
    
    public SimPanel( String symbol ) throws Exception {
        this.history = new EquityHistEODAPIClient().getEquityEODHistory( symbol ) ;
        
        this.priceChart  = new PriceChart( symbol ) ;
        this.volumeChart = new VolumeChart( symbol ) ;
        this.macdChart   = new MACDChart( symbol ) ;
        this.rsiChart    = new RSIChart( symbol ) ;
        this.adxChart    = new ADXChart( symbol ) ;
        
        this.charts = new IndicatorChart[]{
                priceChart,
                volumeChart,
                macdChart,
                rsiChart,
                adxChart
        } ;
        
        this.controlPanel = new SimControlPanel( this ) ;
        
        setUpUI() ;
    }
    
    private void setUpUI() {
        
        volumeChart.setPreferredSize( new Dimension( 100, VOL_CHART_HEIGHT ) ) ;
        macdChart.setPreferredSize( new Dimension( 100, MACD_CHART_HEIGHT ) ) ;
        rsiChart.setPreferredSize( new Dimension( 100, RSI_CHART_HEIGHT ) ) ;
        adxChart.setPreferredSize( new Dimension( 100, ADX_CHART_HEIGHT ) ) ;
        
        priceChart.hideXAxis() ;
        volumeChart.hideXAxis() ;
        macdChart.hideXAxis() ;
        rsiChart.hideXAxis() ;

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
        
        JPanel panel1 = getNewJPanel() ;
        panel1.setLayout( new GridLayout( 3, 1 ) );
        panel1.add( this.macdChart ) ;
        panel1.add( this.rsiChart ) ;
        panel1.add( this.adxChart ) ;
        
        JPanel panel = getNewJPanel() ;
        panel.add( this.volumeChart, BorderLayout.NORTH ) ;
        panel.add( panel1, BorderLayout.CENTER ) ;
        
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
