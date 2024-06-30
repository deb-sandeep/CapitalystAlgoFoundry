package com.sandy.capitalyst.algofoundry.app.bt.api;

import com.sandy.capitalyst.algofoundry.strategy.StrategyConfig;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries;
import com.sandy.capitalyst.algofoundry.strategy.signal.SignalStrategy;
import com.sandy.capitalyst.algofoundry.strategy.tradebook.TradeBook;
import lombok.Getter;

public class StrategyBacktester {
    
    @Getter private final String    symbol ;
    @Getter private final TradeBook tradeBook ;
    
    private final SignalStrategy signalStrategy ;
    
    public StrategyBacktester( String symbol,
                               SignalStrategy signalStrategy,
                               TradeBook tradeBook ) {
        
        this.symbol         = symbol ;
        this.signalStrategy = signalStrategy ;
        this.tradeBook      = tradeBook ;
    }
    
    public TradeBook backtest() {
        
        final CandleSeries history = this.signalStrategy.getCandleSeries() ;
        history.removeAllDayValueListeners() ;
        
        this.tradeBook.clearState() ;
        this.signalStrategy.clear() ;
        this.signalStrategy.addStrategyEventListener( this.tradeBook ) ;
        
        history.addDayValueListener( signalStrategy ) ;
        history.addDayValueListener( tradeBook ) ;
        
        for( int i=0; i<history.getBarCount(); i++ ) {
            history.emitValue( i, CandleSeries.DayValueType.OHLCV ) ;
        }
        
        this.tradeBook.print() ;
        return this.tradeBook ;
    }
}
