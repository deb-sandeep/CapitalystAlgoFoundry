package com.sandy.capitalyst.algofoundry.ui.panel.sim;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityHistEODAPIClient;
import com.sandy.capitalyst.algofoundry.trigger.TradeRule;
import com.sandy.capitalyst.algofoundry.trigger.TradeTrigger;
import com.sandy.capitalyst.algofoundry.trigger.TradeTriggerEvaluator;
import com.sandy.capitalyst.algofoundry.trigger.TradeTriggerListener;
import com.sandy.capitalyst.algofoundry.trigger.rule.EMADownCrossoverRule;
import com.sandy.capitalyst.algofoundry.trigger.rule.EMAUpCrossoverRule;
import com.sandy.capitalyst.algofoundry.ui.indchart.*;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import static com.sandy.capitalyst.algofoundry.core.ui.SwingUtils.getNewJPanel;
import static com.sandy.capitalyst.algofoundry.core.ui.SwingUtils.initPanelUI;
import static com.sandy.capitalyst.algofoundry.core.util.StringUtil.fmtDate;
import static com.sandy.capitalyst.algofoundry.core.util.StringUtil.isEmptyOrNull;

@Slf4j
public class SimPanel extends JPanel {
    
    private static final int VOL_CHART_HEIGHT = 100 ;
    private static final int MACD_CHART_HEIGHT = 150 ;
    private static final int RSI_CHART_HEIGHT = 150 ;
    private static final int ADX_CHART_HEIGHT = 150 ;
    
    private final Map<String, TradeRule> buyRuleMap = new HashMap<>() ;
    private final Map<String, TradeRule> sellRuleMap = new HashMap<>() ;
    
    private final EquityEODHistory history ;
    
    private final IndicatorChart priceChart ;
    private final IndicatorChart volumeChart ;
    private final IndicatorChart macdChart ;
    private final IndicatorChart rsiChart ;
    private final IndicatorChart adxChart ;
    
    private final IndicatorChart[] charts ;
    
    private final SimControlPanel controlPanel ;
    
    private int curBarSeriesIndex = 0 ;
    
    private TradeRule buyRule = null ;
    private TradeRule sellRule = null ;
    private TradeTriggerEvaluator tradeTriggerEvaluator = null ;
    
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
        
        for( IndicatorChart chart : charts ) {
            this.history.addDayValueListener( chart ) ;
        }
        
        populateBuyRulesMap() ;
        populateSellRulesMap() ;
        
        this.controlPanel = new SimControlPanel( this ) ;
        
        setUpUI() ;

        doPrePlayProcessing() ;
    }
    
    private void populateBuyRulesMap() {
        buyRuleMap.put( "EMA_5_20_Crossover", new EMAUpCrossoverRule( history, 5, 20 ) ) ;
        buyRule = buyRuleMap.get( "EMA_5_20_Crossover" ) ;
    }
    
    private void populateSellRulesMap() {
        sellRuleMap.put( "EMA_5_20_Crossover", new EMADownCrossoverRule( history, 5, 20 ) ) ;
        sellRule = sellRuleMap.get( "EMA_5_20_Crossover" ) ;
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
    
    public Collection<String> getBuyRuleNames() {
        return buyRuleMap.keySet() ;
    }
    
    public Collection<String> getSellRuleNames() {
        return sellRuleMap.keySet() ;
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
    
    public void setBuyRule( String ruleName ) {
        buyRule = isEmptyOrNull( ruleName ) ? null : buyRuleMap.get( ruleName ) ;
    }
    
    public void setSellRule( String ruleName ) {
        sellRule = isEmptyOrNull( ruleName ) ? null : sellRuleMap.get( ruleName ) ;
    }
    
    public void doPrePlayProcessing() {
        refreshTradeTriggerEvaluator() ;
    }
    
    private void refreshTradeTriggerEvaluator() {
        if( tradeTriggerEvaluator != null ) {
            history.removeDayValueListener( tradeTriggerEvaluator ) ;
        }
        tradeTriggerEvaluator = new TradeTriggerEvaluator( history, buyRule, sellRule ) ;
        tradeTriggerEvaluator.addTradeTriggerListener( ( TradeTriggerListener )this.priceChart ) ;
        history.addDayValueListener( tradeTriggerEvaluator ) ;
    }
}
