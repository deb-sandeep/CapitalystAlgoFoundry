package com.sandy.capitalyst.algofoundry.app.bt.api;

import com.sandy.capitalyst.algofoundry.strategy.StrategyConfig;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.Candle;
import com.sandy.capitalyst.algofoundry.strategy.signal.SignalStrategy;
import com.sandy.capitalyst.algofoundry.strategy.tradebook.TradeBook;
import lombok.Getter;

import java.util.List;

public class StrategyBacktester {
    
    @Getter private final String    symbol ;
    @Getter private final TradeBook tradeBook ;
    
    private final StrategyConfig config ;
    private final SignalStrategy signalStrategy ;
    
    public StrategyBacktester( String symbol,
                               StrategyConfig config,
                               SignalStrategy signalStrategy,
                               TradeBook tradeBook ) {
        this.symbol         = symbol ;
        this.config         = config ;
        this.signalStrategy = signalStrategy ;
        this.tradeBook      = tradeBook ;
    }
    
    public TradeBook backtest( List<Candle> candles ) {
        return this.tradeBook ;
    }
}
