package com.sandy.capitalyst.algofoundry.ui.panel.sim;

import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityHistEODAPIClient;
import com.sandy.capitalyst.algofoundry.core.ui.IndicatorChart;
import com.sandy.capitalyst.algofoundry.ui.indchart.MACDChart;
import com.sandy.capitalyst.algofoundry.ui.indchart.PriceChart;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.core.ui.SwingUtils.getNewJPanel;
import static com.sandy.capitalyst.algofoundry.core.ui.SwingUtils.initPanelUI;
import static com.sandy.capitalyst.algofoundry.core.indicator.IndicatorType.*;

@Slf4j
public class SimPanel extends JPanel {
    
    private static final int CHART_HEIGHT = 150 ;
    
    private final EquityEODHistory history ;
    
    private final IndicatorChart mainChart ;
    private final IndicatorChart macdChart ;
    
    private final SimControlPanel controlPanel ;
    private final List<String> seriesKeys = new ArrayList<>() ;
    private final Object[][] indChartMapping ;
    
    private int curBarSeriesIndex = 0 ;
    
    public SimPanel( String symbol ) throws Exception {
        this.history = new EquityHistEODAPIClient().getEquityEODHistory( symbol ) ;
        
        this.mainChart = new PriceChart( symbol ) ;
        this.macdChart = new MACDChart( symbol ) ;
        
        this.controlPanel = new SimControlPanel( this ) ;
        
        this.indChartMapping = new Object[][]{
          { macdChart, MACD, MACD_SIGNAL, MACD_HIST }
        } ;
        
        addSeries( CLOSING_PRICE ) ;
        addSeries( BOLLINGER_LOW, BOLLINGER_MID, BOLLINGER_UP ) ;
        addSeries( MACD, MACD_SIGNAL, MACD_HIST ) ;
        
        setUpUI() ;
    }
    
    public synchronized void addSeries( String... seriesKeys ) {
        for( String seriesKey : seriesKeys ) {
            this.seriesKeys.add( seriesKey ) ;
            getCharts( seriesKey ).forEach( c -> c.addSeries( seriesKey ) );
        }
    }
    
    public synchronized void clearChart() {
        for( String seriesKey : this.seriesKeys ) {
            this.mainChart.clearSeries( seriesKey ) ;
        }
    }
    
    private List<IndicatorChart> getCharts( String series ) {
        List<IndicatorChart> chartList = new ArrayList<>() ;
        for( Object[] mapping : indChartMapping ) {
            if( mapping.length > 1 ) {
                for( int j = 1; j < mapping.length; j++ ) {
                    if( series.equals( mapping[j] ) ) {
                        chartList.add( ( IndicatorChart )mapping[0] );
                    }
                }
            }
        }
        if( chartList.isEmpty() ) {
            chartList.add( this.mainChart ) ;
        }
        return chartList ;
    }
    
    private void setUpUI() {
        
        macdChart.setPreferredSize( new Dimension( 100, CHART_HEIGHT ) ) ;

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
        panel.add( this.macdChart ) ;
        return panel ;
    }
    
    public boolean playCurrentBarSeriesData() {
        if( curBarSeriesIndex < history.getBarCount() ) {
            history.emitValues( curBarSeriesIndex, seriesKeys ) ;
            this.curBarSeriesIndex++ ;
            return true ;
        }
        return false ;
    }
    
    public void restartSimulation() {
        this.curBarSeriesIndex = 0 ;
        this.clearChart() ;
    }
}
