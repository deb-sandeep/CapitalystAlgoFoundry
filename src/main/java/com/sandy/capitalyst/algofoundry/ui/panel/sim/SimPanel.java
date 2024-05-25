package com.sandy.capitalyst.algofoundry.ui.panel.sim;

import com.sandy.capitalyst.algofoundry.eodhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityHistEODAPIClient;
import com.sandy.capitalyst.algofoundry.strategy.signal.AbstractZonedSignalStrategy;
import com.sandy.capitalyst.algofoundry.strategy.signal.MySignalStrategy;
import com.sandy.capitalyst.algofoundry.strategy.signal.SignalStrategyEventListener;
import com.sandy.capitalyst.algofoundry.strategy.trade.DefaultTradeBook;
import com.sandy.capitalyst.algofoundry.tradebook.TradeBook;
import com.sandy.capitalyst.algofoundry.tradebook.TradeBookListener;
import com.sandy.capitalyst.algofoundry.ui.indchart.*;
import com.sandy.capitalyst.algofoundry.ui.indchart.util.CrossHairMoveListener;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import static com.sandy.capitalyst.algofoundry.core.ui.SwingUtils.getNewJPanel;
import static com.sandy.capitalyst.algofoundry.core.ui.SwingUtils.initPanelUI;
import static com.sandy.capitalyst.algofoundry.AlgoFoundry.* ;

@Slf4j
public class SimPanel extends JPanel {
    
    private static final int VOL_CHART_HEIGHT = 100 ;
    private static final int MACD_CHART_HEIGHT = 150 ;
    private static final int RSI_CHART_HEIGHT = 150 ;
    private static final int ADX_CHART_HEIGHT = 150 ;
    
    private final Map<String, AbstractZonedSignalStrategy> tradeStrategyMap = new LinkedHashMap<>() ;
    
    private final EquityEODHistory history ;
    
    private final IndicatorChart priceChart ;
    private final IndicatorChart volumeChart ;
    private final IndicatorChart macdChart ;
    private final IndicatorChart rsiChart ;
    private final IndicatorChart adxChart ;
    
    private final IndicatorChart[] charts ;
    
    private final SimControlPanel controlPanel ;
    
    private int curBarSeriesIndex = 0 ;
    
    private AbstractZonedSignalStrategy tradeStrategy = null ;
    
    private TradeBook tradeBook = new DefaultTradeBook() ;
    
    public SimPanel( String symbol ) throws Exception {
        
        EquityHistEODAPIClient apiClient = getBean( EquityHistEODAPIClient.class ) ;
        
        this.history = new EquityEODHistory( symbol,
                                     apiClient.getHistoricCandles( symbol ) ) ;
        
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
        
        for( IndicatorChart chart : charts ) {
            this.history.addDayValueListener( chart ) ;
        }
        
        (( PriceChart )priceChart).addCrosshairMoveListeners(
                ( CrossHairMoveListener )macdChart,
                ( CrossHairMoveListener )rsiChart,
                ( CrossHairMoveListener )adxChart ) ;
        
        populateTradeStrategiesMap() ;

        this.controlPanel = new SimControlPanel( this ) ;
        this.history.addDayValueListener( tradeBook ) ;
        this.tradeBook.addListener( (TradeBookListener)priceChart ) ;
        
        setUpUI() ;
        doPrePlayProcessing() ;
    }
    
    private void populateTradeStrategiesMap() {
        tradeStrategyMap.put( MySignalStrategy.NAME,
                              new MySignalStrategy( history ) ) ;
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
    
    public Collection<String> getTradeStrategyNames() {
        return tradeStrategyMap.keySet() ;
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
        controlPanel.getLogDisplayWidget().clear() ;
        tradeBook.clearState() ;
    }
    
    public void setTradeStrategy( String strategyName ) {
        if( tradeStrategy != null ) {
            tradeStrategy.clear() ;
            history.removeDayValueListener( tradeStrategy ) ;
        }
        
        tradeStrategy = tradeStrategyMap.get( strategyName ) ;
        
        tradeStrategy.addStrategyEventListener( controlPanel.getLogDisplayWidget() ) ;
        tradeStrategy.addStrategyEventListener( ( SignalStrategyEventListener )volumeChart ) ;
        tradeStrategy.addStrategyEventListener( ( SignalStrategyEventListener )priceChart ) ;
        tradeStrategy.addStrategyEventListener( tradeBook ) ;
        
        history.addDayValueListener( tradeStrategy ) ;
    }
    
    public void doPrePlayProcessing() {}
    
    public TradeBook getTradeBook() {
        return this.tradeBook ;
    }
}
