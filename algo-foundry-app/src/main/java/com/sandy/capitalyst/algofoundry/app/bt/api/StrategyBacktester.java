package com.sandy.capitalyst.algofoundry.app.bt.api;

import com.sandy.capitalyst.algofoundry.strategy.impl.MySignalStrategy;
import com.sandy.capitalyst.algofoundry.strategy.impl.MyStrategyConfig;
import com.sandy.capitalyst.algofoundry.strategy.impl.MyTradeBook;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.Candle;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries;
import com.sandy.capitalyst.algofoundry.strategy.signal.SignalStrategy;
import com.sandy.capitalyst.algofoundry.strategy.tradebook.TradeBook;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class StrategyBacktester {
    
    @Getter private final String       symbol ;
    @Getter private final List<Candle> candles ;
    
    private final MyStrategyConfig config;
    
    public StrategyBacktester( String symbol, List<Candle> candles, MyStrategyConfig config ) {
        this.symbol = symbol ;
        this.config = config ;
        this.candles = candles ;
    }
    
    public TradeBook backtest() {
        
        CandleSeries   candleSeries   = new CandleSeries( symbol, candles, config ) ;
        SignalStrategy signalStrategy = new MySignalStrategy( candleSeries, config ) ;
        TradeBook      tradeBook      = new MyTradeBook( symbol, config ) ;

        signalStrategy.addStrategyEventListener( tradeBook ) ;
        
        candleSeries.addDayValueListener( signalStrategy ) ;
        candleSeries.addDayValueListener( tradeBook ) ;
        
        for( int i=0; i<candleSeries.getBarCount(); i++ ) {
            candleSeries.emitValue( i, CandleSeries.DayValueType.OHLCV ) ;
        }
        return tradeBook ;
    }
}
