package com.sandy.capitalyst.algofoundry.strategy;

import java.util.Date;

public interface StrategyZoneListener {
    
    void handleZone( Date date, AbstractZonedTradeStrategy.Zone zone, double volume ) ;
}
