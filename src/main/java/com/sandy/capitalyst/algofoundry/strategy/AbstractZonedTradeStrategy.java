package com.sandy.capitalyst.algofoundry.strategy;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.event.CurrentZoneEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.strategy.event.CurrentZoneEvent.* ;

public abstract class AbstractZonedTradeStrategy extends AbstractTradeStrategy {
    
    public enum Zone { BLACKOUT, LOOKOUT, ENTRY_ACTIVE, EXIT_ACTIVE } ;
    
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
    
    public final TradeSignal executeSignalStrategy( int index ) {
        
        TradeSignal signal = null ;
        double closingPrice = bar.getClosePrice().doubleValue() ;
        double volume = bar.getVolume().doubleValue() ;
        
        if( isInBlackoutPeriod() ) {
            publishCurrentZone( ZoneType.BLACKOUT, MovementType.CURRENT ) ;
            blackoutDaysLeft-- ;
        }
        else if( !( isInEntryActivePeriod() || isInExitActivePeriod() ) ) {
            logger.log( date, "Lookout", true ) ;
            publishCurrentZone( ZoneType.LOOKOUT, MovementType.CURRENT ) ;
            if( isEntryZoneTriggered( index ) ) {
                logger.log2( ">> ENTRY ZONE ACTIVATED" ) ;
                publishCurrentZone( ZoneType.BUY, MovementType.ENTRY ) ;
                activeEntryDaysLeft = ACTIVATION_WINDOW ;
                activeExitDaysLeft = 0 ;
            }
            else if( isExitZoneTriggered( index ) ) {
                logger.log2( ">> EXIT ZONE ACTIVATED" ) ;
                publishCurrentZone( ZoneType.SELL, MovementType.ENTRY ) ;
                activeEntryDaysLeft = 0 ;
                activeExitDaysLeft = ACTIVATION_WINDOW ;
            }
        }
        
        if( isInEntryActivePeriod() ) {
            logger.log( date, "Entry Check " + getNumDaysIntoEntryZone(), true ) ;
            publishCurrentZone( ZoneType.BUY, MovementType.CURRENT ) ;
            if( isEntryConditionMet( index ) ) {
                logger.log2( ">> ENTRY" ) ;
                signal = new TradeSignal( TradeSignal.Type.ENTRY, date,
                        history.getSymbol(), closingPrice ) ;
            }
            else {
                logger.log2( ">> Entry disqualified" ) ;
            }
            activeEntryDaysLeft-- ;
            if( isExitZoneTriggered( index ) ) {
                logger.log2( ">> EXIT ZONE ACTIVATED" ) ;
                publishCurrentZone( ZoneType.SELL, MovementType.ENTRY ) ;
                activeEntryDaysLeft = 0 ;
                activeExitDaysLeft = ACTIVATION_WINDOW ;
            }
        }
        else if( isInExitActivePeriod() ) {
            logger.log( date, "Exit Check " + getNumDaysIntoExitZone(), true ) ;
            publishCurrentZone( ZoneType.SELL, MovementType.CURRENT ) ;
            if( isExitConditionMet( index ) ) {
                logger.log1( "EXIT" ) ;
                signal = new TradeSignal( TradeSignal.Type.EXIT, date,
                                          history.getSymbol(), closingPrice ) ;
            }
            else {
                logger.log2( ">> Exit disqualified" ) ;
            }
            activeExitDaysLeft-- ;
            if( isEntryZoneTriggered( index ) ) {
                logger.log2( ">> ENTRY ZONE ACTIVATED" ) ;
                publishCurrentZone( ZoneType.SELL, MovementType.ENTRY ) ;
                activeEntryDaysLeft = ACTIVATION_WINDOW ;
                activeExitDaysLeft = 0 ;
            }
        }
        
        if( signal != null ) {
            activeExitDaysLeft = 0 ;
            activeEntryDaysLeft = 0 ;
            blackoutDaysLeft = POST_TRADE_COOLOFF_PERIOD ;
        }
        
        return signal ;
    }
    
    protected abstract boolean isEntryZoneTriggered( int index ) ;
    
    protected abstract boolean isExitZoneTriggered( int index ) ;
    
    protected abstract boolean isEntryConditionMet( int index ) ;
    
    protected abstract boolean isExitConditionMet( int index ) ;
    
}
