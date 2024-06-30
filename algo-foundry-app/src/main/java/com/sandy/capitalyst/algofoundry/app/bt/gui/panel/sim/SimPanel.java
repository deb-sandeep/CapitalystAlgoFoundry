package com.sandy.capitalyst.algofoundry.app.bt.gui.panel.sim;

import com.sandy.capitalyst.algofoundry.app.AlgoFoundry;
import com.sandy.capitalyst.algofoundry.app.apiclient.histeod.EquityHistEODAPIClient;
import com.sandy.capitalyst.algofoundry.app.bt.api.StrategyBacktester;
import com.sandy.capitalyst.algofoundry.app.bt.gui.indchart.*;
import com.sandy.capitalyst.algofoundry.app.core.ui.SwingUtils;
import com.sandy.capitalyst.algofoundry.app.bt.gui.indchart.util.CrossHairMoveListener;
import com.sandy.capitalyst.algofoundry.strategy.impl.MySignalStrategy;
import com.sandy.capitalyst.algofoundry.strategy.impl.MyStrategyConfig;
import com.sandy.capitalyst.algofoundry.strategy.impl.MyTradeBook;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.Candle;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries;
import com.sandy.capitalyst.algofoundry.strategy.signal.SignalStrategy;
import com.sandy.capitalyst.algofoundry.strategy.signal.SignalStrategyEventListener;
import com.sandy.capitalyst.algofoundry.strategy.signal.ZonedSignalStrategy;
import com.sandy.capitalyst.algofoundry.strategy.tradebook.TradeBook;
import com.sandy.capitalyst.algofoundry.strategy.tradebook.TradeBookListener;
import com.sandy.capitalyst.algofoundry.strategy.util.StrategyConfigUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.app.AlgoFoundry.getBean;

@Slf4j
public class SimPanel extends JPanel {
    
    private static final int VOL_CHART_HEIGHT = 100 ;
    private static final int MACD_CHART_HEIGHT = 150 ;
    private static final int RSI_CHART_HEIGHT = 150 ;
    private static final int ADX_CHART_HEIGHT = 150 ;
    
    private final Map<String, ZonedSignalStrategy> tradeStrategyMap = new LinkedHashMap<>() ;
    
    private final CandleSeries history ;
    
    private final IndicatorChart priceChart ;
    private final IndicatorChart volumeChart ;
    private final IndicatorChart macdChart ;
    private final IndicatorChart rsiChart ;
    private final IndicatorChart adxChart ;
    
    private final IndicatorChart[] charts ;
    
    private final SimControlPanel controlPanel ;
    
    private int curBarSeriesIndex = 0 ;
    
    private ZonedSignalStrategy tradeStrategy = null ;
    
    private final TradeBook tradeBook ;
    private final MyStrategyConfig config ;
    
    @Getter private final String symbol ;
    
    public SimPanel( String symbol ) throws Exception {
        
        EquityHistEODAPIClient apiClient = getBean( EquityHistEODAPIClient.class ) ;
        List<Candle> candles = apiClient.getHistoricCandles( symbol ) ;
        
        this.symbol = symbol ;
        this.config = new MyStrategyConfig() ;
        StrategyConfigUtil.populateStrategyConfig( this.config, AlgoFoundry.getConfig() ) ;
        
        this.tradeBook = new MyTradeBook( symbol, config ) ;
        this.history = new CandleSeries( symbol, candles, config ) ;
        
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
        
        log.info( "Backtesting" ) ;
        CandleSeries hist = new CandleSeries( symbol, candles, config ) ;
        SignalStrategy sigStrat = new MySignalStrategy( hist, config ) ;
        TradeBook tb = new MyTradeBook( symbol, config ) ;
        
        StrategyBacktester backtester = new StrategyBacktester( symbol, sigStrat, tb ) ;
        backtester.backtest() ;
        log.info( "Finished backtesting" );
    }
    
    private void populateTradeStrategiesMap() {
        tradeStrategyMap.put( MySignalStrategy.NAME,
                              new MySignalStrategy( history, config ) ) ;
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

        SwingUtils.initPanelUI( this ) ;
        add( getChartPanel(), BorderLayout.CENTER ) ;
        add( controlPanel, BorderLayout.EAST ) ;
    }
    
    private JPanel getChartPanel() {
        JPanel panel = SwingUtils.getNewJPanel() ;
        panel.add( priceChart, BorderLayout.CENTER ) ;
        panel.add( getFooterChartPanel(), BorderLayout.SOUTH ) ;
        return panel ;
    }
    
    private JPanel getFooterChartPanel() {
        
        JPanel panel1 = SwingUtils.getNewJPanel() ;
        panel1.setLayout( new GridLayout( 3, 1 ) );
        panel1.add( this.macdChart ) ;
        panel1.add( this.rsiChart ) ;
        panel1.add( this.adxChart ) ;
        
        JPanel panel = SwingUtils.getNewJPanel() ;
        panel.add( this.volumeChart, BorderLayout.NORTH ) ;
        panel.add( panel1, BorderLayout.CENTER ) ;
        
        return panel ;
    }
    
    public Collection<String> getTradeStrategyNames() {
        return tradeStrategyMap.keySet() ;
    }
    
    public boolean playCurrentBarSeriesData() {
        
        Set<CandleSeries.DayValueType> dayValueTypes = new HashSet<>() ;
        for( IndicatorChart chart : charts ) {
            dayValueTypes.addAll( chart.getConsumedPayloadTypes() ) ;
        }
        
        if( curBarSeriesIndex < history.getBarCount() ) {
            history.emitDayValues( curBarSeriesIndex, dayValueTypes ) ;
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
    
    public void simulationEnded() { tradeBook.print() ; }

    public TradeBook getTradeBook() {
        return this.tradeBook ;
    }
    
}
