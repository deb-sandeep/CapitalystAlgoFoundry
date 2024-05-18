package com.sandy.capitalyst.algofoundry.strategy;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import org.ta4j.core.Bar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractZonedTradeStrategy extends AbstractTradeStrategy {
    
    public enum Zone { BLACKOUT, LOOKOUT, ENTRY_ACTIVE, EXIT_ACTIVE } ;
    
    private static final int ACTIVATION_WINDOW = 5 ;
    private static final int POST_TRADE_COOLOFF_PERIOD = 3 ;
    
    private int blackoutDaysLeft    = 20 ;
    private int activeEntryDaysLeft = 0 ;
    private int activeExitDaysLeft  = 0 ;
    
    private final List<StrategyZoneListener> zoneListeners = new ArrayList<>() ;
    
    protected AbstractZonedTradeStrategy( EquityEODHistory history ) {
        super( history ) ;
    }
    
    public void clear() {
        super.clear() ;
        this.blackoutDaysLeft = 20 ;
        this.activeEntryDaysLeft = 0 ;
        this.activeExitDaysLeft = 0 ;
    }
    
    public void addZoneListener( StrategyZoneListener listener ) {
        if( listener != null ) {
            this.zoneListeners.add( listener ) ;
        }
    }
    
    public void removeZoneListeners() {
        this.zoneListeners.clear() ;
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
    
    public final TradeSignal executeStrategy( int index, Date date, Bar bar ) {
        
        TradeSignal signal = null ;
        double closingPrice = bar.getClosePrice().doubleValue() ;
        double volume = bar.getVolume().doubleValue() ;
        
        if( isInBlackoutPeriod() ) {
            logger.log( date, "Blackout" ) ;
            publishCurrentZone( date, Zone.BLACKOUT, volume ) ;
            blackoutDaysLeft-- ;
        }
        else if( !( isInEntryActivePeriod() || isInExitActivePeriod() ) ) {
            logger.log( date, "Lookout", true ) ;
            publishCurrentZone( date, Zone.LOOKOUT, volume ) ;
            if( isEntryZoneTriggered( index ) ) {
                logger.log1( "ENTRY ZONE ACTIVATED" ) ;
                activeEntryDaysLeft = ACTIVATION_WINDOW ;
                activeExitDaysLeft = 0 ;
            }
            else if( isExitZoneTriggered( index ) ) {
                logger.log1( "EXIT ZONE ACTIVATED" ) ;
                activeEntryDaysLeft = 0 ;
                activeExitDaysLeft = ACTIVATION_WINDOW ;
            }
        }
        else if( isInEntryActivePeriod() ) {
            logger.log( date, "Entry Check " + getNumDaysIntoEntryZone(), true ) ;
            publishCurrentZone( date, Zone.ENTRY_ACTIVE, volume ) ;
            if( isEntryConditionMet( index ) ) {
                logger.log1( "ENTRY" ) ;
                signal = new TradeSignal( TradeSignal.Type.ENTRY, date,
                        history.getSymbol(), closingPrice ) ;
            }
            else {
                logger.log1( "Entry disqualified" ) ;
            }
            activeEntryDaysLeft-- ;
        }
        else if( isInExitActivePeriod() ) {
            logger.log( date, "Exit Check " + getNumDaysIntoExitZone(), true ) ;
            publishCurrentZone( date, Zone.EXIT_ACTIVE, volume ) ;
            if( isExitConditionMet( index ) ) {
                logger.log1( "EXIT" ) ;
                signal = new TradeSignal( TradeSignal.Type.EXIT, date,
                        history.getSymbol(), closingPrice ) ;
            }
            else {
                logger.log1( "Exit disqualified" ) ;
            }
            activeExitDaysLeft-- ;
        }
        
        if( signal != null ) {
            activeExitDaysLeft = 0 ;
            activeEntryDaysLeft = 0 ;
            blackoutDaysLeft = POST_TRADE_COOLOFF_PERIOD ;
        }
        
        return signal ;
    }
    
    private void publishCurrentZone( Date date, Zone zone, double volume ) {
        zoneListeners.forEach( l -> l.handleZone( date, zone, volume ) ) ;
    }
    
    protected abstract boolean isEntryZoneTriggered( int index ) ;
    
    protected abstract boolean isExitZoneTriggered( int index ) ;
    
    protected abstract boolean isEntryConditionMet( int index ) ;
    
    protected abstract boolean isExitConditionMet( int index ) ;
    
}
