package com.sandy.capitalyst.algofoundry.strategy.event;

import com.sandy.capitalyst.algofoundry.strategy.StrategyEvent;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

public class CurrentZoneEvent extends StrategyEvent {
    
    public enum ZoneType { BLACKOUT, LOOKOUT, BUY, SELL } ;
    public enum MovementType { ENTRY, EXIT, CURRENT } ;
    
    @Getter private final ZoneType zoneType ;
    @Getter private final MovementType movementType ;
    
    public CurrentZoneEvent( Date date, Bar bar, ZoneType zoneType, MovementType movementType ) {
        super( date, bar ) ;
        this.zoneType = zoneType ;
        this.movementType = movementType ;
    }
}
