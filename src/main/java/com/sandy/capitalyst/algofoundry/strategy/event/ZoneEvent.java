package com.sandy.capitalyst.algofoundry.strategy.event;

import com.sandy.capitalyst.algofoundry.strategy.StrategyEvent;
import lombok.Getter;

import java.util.Date;

public class ZoneEvent extends StrategyEvent {
    
    public enum ZoneType { BUY, SELL } ;
    public enum MovementType { ENTRY, EXIT } ;
    
    @Getter private final ZoneType zoneType ;
    @Getter private final MovementType movementType ;
    
    public ZoneEvent( Date date, ZoneType zoneType, MovementType movementType ) {
        super( date ) ;
        this.zoneType = zoneType ;
        this.movementType = movementType ;
    }
}
