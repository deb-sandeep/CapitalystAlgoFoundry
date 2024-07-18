package com.sandy.capitalyst.algofoundry.app.tuner;

import com.sandy.capitalyst.algofoundry.app.apiclient.equitymeta.EquityMeta;
import com.sandy.capitalyst.algofoundry.app.apiclient.histeod.EquityHistEODAPIClient;
import com.sandy.capitalyst.algofoundry.app.backtester.api.StrategyBacktester;
import com.sandy.capitalyst.algofoundry.strategy.impl.MyStrategyConfig;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.Candle;
import com.sandy.capitalyst.algofoundry.strategy.tradebook.TradeBook;
import com.sandy.capitalyst.algofoundry.strategy.util.HParameter;
import com.sandy.capitalyst.algofoundry.strategy.util.StrategyConfigUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.sandy.capitalyst.algofoundry.app.AlgoFoundry.getBean;
import static com.sandy.capitalyst.algofoundry.app.core.util.StringUtil.fmtDbl;

@Slf4j
public class GridSearch {
    
    public interface GridSearchListener {
        default void parametersUpdated() {}
        default void candleLoadingStarted( int numSymbols ) {}
        default void loadingCandles( int index, int numSymbols, String symbol ) {}
        default void candleLoadingEnded() {}
        default void goldenParametersFound( double mktProfit,
                                            HyperParameterGroup parameters,
                                            MyStrategyConfig cfg,
                                            List<TradeBook> tradeBooks ) {}
    }
    
    private final MyStrategyConfig baseConfig;
    private final List<EquityMeta> equityMetaList ;
    private final Map<String, List<Candle>> equityCandles = new LinkedHashMap<>() ;
    
    private final List<GridSearchListener> listeners = new ArrayList<>() ;
    
    private boolean killSwitchFlag = false ;
    
    @Getter
    private final HyperParameterGroup hyperParameters ;
    
    private final Map<String, List<Candle>> stockCandlesMap = new LinkedHashMap<>() ;
    
    public GridSearch( List<EquityMeta> equityMetaList, MyStrategyConfig baseConfig,
                       GridSearchListener listener ) {
        
        this.baseConfig = baseConfig;
        this.equityMetaList = equityMetaList ;
        this.hyperParameters = new HyperParameterGroup( extractHyperParameters() ) ;
        this.listeners.add( listener ) ;
    }
    
    public void addListener( GridSearchListener listener ) {
        listeners.add( listener ) ;
    }
    
    private List<HyperParameter> extractHyperParameters() {
        
        List<HyperParameter> hParamList = new ArrayList<>() ;
        List<Field> hpFields = getHyperParameterFields( new ArrayList<>(),
                                                        this.baseConfig.getClass() ) ;
        for( Field hpField : hpFields ) {
            HParameter     hp     = hpField.getAnnotation( HParameter.class ) ;
            HyperParameter hParam = new HyperParameter( hpField.getName(), hpField.getType(), hp.min(), hp.max(), hp.step() ) ;
            hParamList.add( hParam ) ;
        }
        return hParamList ;
    }
    
    private List<Field> getHyperParameterFields( List<Field> hpFields, Class<?> type ) {
        
        if( type != null ) {
            getHyperParameterFields( hpFields, type.getSuperclass() ) ;
            Field[] fields = type.getDeclaredFields() ;
            for( Field field : fields ) {
                Annotation hpAnnot = field.getAnnotation( HParameter.class ) ;
                if( hpAnnot != null ) {
                    hpFields.add( field ) ;
                }
            }
        }
        return hpFields ;
    }
    
    public void setKillSwitch() {
        killSwitchFlag = true ;
    }
    
    public void tuneHyperParameters() throws Exception {
        
        loadCandles() ;
        
        MyStrategyConfig cfgClone = new MyStrategyConfig() ;
        StrategyConfigUtil.populateStrategyConfig( cfgClone, this.baseConfig ) ;
        
        double bestMarketProfit = Float.NEGATIVE_INFINITY ;
        
        long iterationNo = 1 ;
        
        while( !hyperParameters.isGridExplorationComplete() && !killSwitchFlag ) {
            
            if( !hyperParameters.randomizeGridPoint( cfgClone ) ) {
                killSwitchFlag = true ;
                log.debug( "Could not find a fresh grid point." ) ;
            }
            else {
                listeners.forEach( GridSearchListener::parametersUpdated );
                
                StrategyBacktester bt ;
                TradeBook tb ;
                List<TradeBook> tradeBooks = new ArrayList<>() ;
                
                for( EquityMeta meta : equityMetaList ) {
                    String symbol = meta.getSymbol() ;
                    List<Candle> candles = equityCandles.get( symbol ) ;
                    
                    bt = new StrategyBacktester( symbol, candles, cfgClone ) ;
                    tb = bt.backtest() ;
                    
                    //log.debug( "I{} - {} - {}", iterationNo, symbol, fmtDbl( tb.getTotalProfitPct() ) ) ;
                    tradeBooks.add( tb ) ;
                }
                
                final double mktProfit = getMarketProfit( tradeBooks ) ;
                //log.debug( "I-{} - Profit = {}%", iterationNo, fmtDbl( mktProfit ) );
                if( mktProfit > bestMarketProfit ) {
                    bestMarketProfit = mktProfit ;
                    log.debug( "\tGolden configuration found. {}", fmtDbl( mktProfit ) ) ;
                    
                    MyStrategyConfig finalCfgClone = cfgClone;
                    listeners.forEach( l -> l.goldenParametersFound(
                            mktProfit, hyperParameters, finalCfgClone, tradeBooks
                    ) );
                }
                
                iterationNo++ ;
                cfgClone = new MyStrategyConfig() ;
                StrategyConfigUtil.populateStrategyConfig( cfgClone, this.baseConfig ) ;
            }
        }
        killSwitchFlag = false ;
    }
    
    private double getMarketProfit( List<TradeBook> tradeBooks ) {
        
        double totalProfit = 0 ;
        double totalBuyPrice = 0 ;
        
        for( TradeBook tb : tradeBooks ) {
            totalProfit += tb.getTotalProfit() ;
            totalBuyPrice += tb.getTotalBuyPrice() ;
        }
        
        return ( totalProfit / totalBuyPrice )*100 ;
    }
    
    private void loadCandles() throws Exception {
        
        EquityHistEODAPIClient apiClient = getBean( EquityHistEODAPIClient.class ) ;
        List<Candle> candles ;
        
        listeners.forEach( l -> l.candleLoadingStarted( equityMetaList.size() ) );
        for( int i=0; i<equityMetaList.size() && !killSwitchFlag; i++ ) {
            EquityMeta meta = equityMetaList.get( i ) ;
            String symbol = meta.getSymbol() ;
            
            candles = apiClient.getHistoricCandles( symbol ) ;
            
            int idx = i + 1 ;
            listeners.forEach( l -> l.loadingCandles( idx, equityMetaList.size(), symbol ) ) ;
            equityCandles.put( meta.getSymbol(), candles ) ;
            Thread.sleep( 5 ) ;
        }
        listeners.forEach( GridSearchListener::candleLoadingEnded ) ;
    }
}
