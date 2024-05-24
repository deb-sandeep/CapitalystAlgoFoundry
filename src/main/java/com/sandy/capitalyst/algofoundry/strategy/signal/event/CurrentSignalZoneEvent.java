package com.sandy.capitalyst.algofoundry.strategy.signal.event;

import com.sandy.capitalyst.algofoundry.strategy.signal.SignalStrategyEvent;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

public class CurrentSignalZoneEvent extends SignalStrategyEvent {
    
    public enum ZoneType { BLACKOUT, LOOKOUT, BUY, SELL } ;
    public enum MovementType { ENTRY, EXIT, CURRENT } ;
    
    @Getter private final ZoneType zoneType ;
    @Getter private final MovementType movementType ;
    
    public CurrentSignalZoneEvent( Date date, Bar bar, ZoneType zoneType, MovementType movementType ) {
        super( date, bar ) ;
        this.zoneType = zoneType ;
        this.movementType = movementType ;
    }
}
