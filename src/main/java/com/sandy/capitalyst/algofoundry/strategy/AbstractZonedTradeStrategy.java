package com.sandy.capitalyst.algofoundry.strategy;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.event.CurrentZoneEvent;
import com.sandy.capitalyst.algofoundry.strategy.event.TradeEvent;

import static com.sandy.capitalyst.algofoundry.strategy.event.CurrentZoneEvent.MovementType;
import static com.sandy.capitalyst.algofoundry.strategy.event.CurrentZoneEvent.ZoneType;

public abstract class AbstractZonedTradeStrategy extends AbstractTradeStrategy {
    
    private static final int ACTIVATION_WINDOW = 5 ;
    private static final int POST_TRADE_COOLOFF_PERIOD = 0 ;
    
    private int blackoutDaysLeft    = 20 ;
    private int activeEntryDaysLeft = 0 ;
    private int activeExitDaysLeft  = 0 ;
    
    protected AbstractZonedTradeStrategy( EquityEODHistory history ) {
        super( history ) ;
    }
    
    public void clear() {
        super.clear() ;
        this.blackoutDaysLeft = 20 ;
        this.activeEntryDaysLeft = 0 ;
        this.activeExitDaysLeft = 0 ;
    }
    
    protected boolean isInBlackoutPeriod() {
        return this.blackoutDaysLeft > 0 ;
    }
    
    protected boolean isInEntryActivePeriod() {
        return this.activeEntryDaysLeft > 0 ;
    }
    
    protected boolean isInExitActivePeriod() {
        return this.activeExitDaysLeft > 0 ;
    }
    
    protected int getNumDaysIntoEntryZone() {
        if( isInEntryActivePeriod() ) {
            return ACTIVATION_WINDOW - activeEntryDaysLeft + 1 ;
        }
        return 0 ;
    }
    
    protected int getNumDaysIntoExitZone() {
        if( isInExitActivePeriod() ) {
            return ACTIVATION_WINDOW - activeExitDaysLeft + 1 ;
        }
        return 0 ;
    }
    
    private void publishCurrentZone( ZoneType type, MovementType movementType ) {
        CurrentZoneEvent zoneEvt = new CurrentZoneEvent( date, bar, type, movementType ) ;
        super.publishEvent( zoneEvt ) ;
    }
    
    public final void executeStrategy( int index ) {
        
        double closingPrice = bar.getClosePrice().doubleValue() ;
        
        if( isInBlackoutPeriod() ) {
            publishCurrentZone( ZoneType.BLACKOUT, MovementType.CURRENT ) ;
            blackoutDaysLeft-- ;
        }
        else if( !( isInEntryActivePeriod() || isInExitActivePeriod() ) ) {
            publishCurrentZone( ZoneType.LOOKOUT, MovementType.CURRENT ) ;
            if( isEntryZoneTriggered( index ) ) {
                publishCurrentZone( ZoneType.BUY, MovementType.ENTRY ) ;
                activeEntryDaysLeft = ACTIVATION_WINDOW ;
                activeExitDaysLeft = 0 ;
            }
            else if( isExitZoneTriggered( index ) ) {
                publishCurrentZone( ZoneType.SELL, MovementType.ENTRY ) ;
                activeEntryDaysLeft = 0 ;
                activeExitDaysLeft = ACTIVATION_WINDOW ;
            }
        }
        
        if( isInEntryActivePeriod() ) {
            publishCurrentZone( ZoneType.BUY, MovementType.CURRENT ) ;
            if( isEntryConditionMet( index ) ) {
                publishTradeSignal( TradeEvent.Type.BUY ) ;
            }
            else {
                super.debug2( ">> Entry disqualified" ); ;
            }
            activeEntryDaysLeft-- ;
            if( isExitZoneTriggered( index ) ) {
                publishCurrentZone( ZoneType.SELL, MovementType.ENTRY ) ;
                activeEntryDaysLeft = 0 ;
                activeExitDaysLeft = ACTIVATION_WINDOW ;
            }
        }
        else if( isInExitActivePeriod() ) {
            publishCurrentZone( ZoneType.SELL, MovementType.CURRENT ) ;
            if( isExitConditionMet( index ) ) {
                publishTradeSignal( TradeEvent.Type.SELL ) ;
            }
            else {
                debug2( ">> Exit disqualified" ); ;
            }
            activeExitDaysLeft-- ;
            if( isEntryZoneTriggered( index ) ) {
                publishCurrentZone( ZoneType.SELL, MovementType.ENTRY ) ;
                activeEntryDaysLeft = ACTIVATION_WINDOW ;
                activeExitDaysLeft = 0 ;
            }
        }
    }
    
    protected void publishTradeSignal( TradeEvent.Type type ) {
        super.publishTradeSignal( type ) ;
        activeExitDaysLeft = 0 ;
        activeEntryDaysLeft = 0 ;
    }
    
    protected abstract boolean isEntryZoneTriggered( int index ) ;
    
    protected abstract boolean isExitZoneTriggered( int index ) ;
    
    protected abstract boolean isEntryConditionMet( int index ) ;
    
    protected abstract boolean isExitConditionMet( int index ) ;
    
}
