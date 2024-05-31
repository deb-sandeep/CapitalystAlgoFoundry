package com.sandy.capitalyst.algofoundry.strategy.signal;

import com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.CurrentSignalZoneEvent;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.TradeSignalEvent;

import static com.sandy.capitalyst.algofoundry.strategy.signal.event.CurrentSignalZoneEvent.* ;
import static com.sandy.capitalyst.algofoundry.strategy.signal.event.CurrentSignalZoneEvent.MovementType.* ;
import static com.sandy.capitalyst.algofoundry.strategy.signal.event.CurrentSignalZoneEvent.ZoneType.* ;

public abstract class AbstractZonedSignalStrategy extends AbstractSignalStrategy {
    
    private static final int ACTIVATION_WINDOW = 5 ;
    
    private int blackoutDaysLeft    = 20 ;
    private int activeEntryDaysLeft = 0 ;
    private int activeExitDaysLeft  = 0 ;
    
    protected AbstractZonedSignalStrategy( CandleSeries history ) {
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
    
    protected boolean isInBuyActivePeriod() {
        return this.activeEntryDaysLeft > 0 ;
    }
    
    protected boolean isInSellActivePeriod() {
        return this.activeExitDaysLeft > 0 ;
    }
    
    protected int getNumDaysIntoEntryZone() {
        if( isInBuyActivePeriod() ) {
            return ACTIVATION_WINDOW - activeEntryDaysLeft + 1 ;
        }
        return 0 ;
    }
    
    protected int getNumDaysIntoExitZone() {
        if( isInSellActivePeriod() ) {
            return ACTIVATION_WINDOW - activeExitDaysLeft + 1 ;
        }
        return 0 ;
    }
    
    private void pubCurrentZone( ZoneType type ) {
        super.publishEvent( new CurrentSignalZoneEvent( date, bar, type, CURRENT ) ) ;
    }
    
    private void pubBuyZoneEntry() {
        super.publishEvent( new CurrentSignalZoneEvent( date, bar, BUY, ENTRY ) ) ;
        activeEntryDaysLeft = ACTIVATION_WINDOW ;
        activeExitDaysLeft = 0 ;
    }
    
    private void pubSellZoneEntry() {
        super.publishEvent( new CurrentSignalZoneEvent( date, bar, SELL, ENTRY ) ) ;
        activeEntryDaysLeft = 0 ;
        activeExitDaysLeft = ACTIVATION_WINDOW ;
    }
    
    private void pubBuyZoneExit() {
        super.publishEvent( new CurrentSignalZoneEvent( date, bar, BUY, EXIT ) ) ;
        activeEntryDaysLeft = activeExitDaysLeft = 0 ;
    }
    
    private void pubSellZoneExit() {
        super.publishEvent( new CurrentSignalZoneEvent( date, bar, SELL, EXIT ) ) ;
    }
    
    private void lookoutForZoneChange( int index, ZoneType... lookoutZoneTypes ) {
        for( ZoneType zoneType : lookoutZoneTypes ) {
            if( zoneType == BUY ) {
                if( isBuyZoneActivated( index ) ) {
                    if( isInSellActivePeriod() ) {
                        pubSellZoneExit() ;
                    }
                    pubBuyZoneEntry() ;
                }
            }
            else if( zoneType == SELL ) {
                if( isSellZoneActivated( index ) ) {
                    if( isInBuyActivePeriod() ) {
                        pubBuyZoneExit() ;
                    }
                    pubSellZoneEntry() ;
                }
            }
        }
    }
    
    public final void executeStrategy( int index ) {
        
        if( isInBlackoutPeriod() ) {
            pubCurrentZone( BLACKOUT ) ;
            blackoutDaysLeft-- ;
        }
        else if( !( isInBuyActivePeriod() || isInSellActivePeriod() ) ) {
            pubCurrentZone( LOOKOUT ) ;
            lookoutForZoneChange( index, BUY, SELL ) ;
        }
        
        if( isInBuyActivePeriod() ) {
            pubCurrentZone( BUY ) ;
            if( isBuyConditionMet( index ) ) {
                pubTradeSignal( TradeSignalEvent.Type.BUY ) ;
            }
            activeEntryDaysLeft-- ;
            lookoutForZoneChange( index, SELL ) ;
        }
        else if( isInSellActivePeriod() ) {
            pubCurrentZone( SELL ) ;
            if( isSellConditionMet( index ) ) {
                pubTradeSignal( TradeSignalEvent.Type.SELL ) ;
            }
            activeExitDaysLeft-- ;
            lookoutForZoneChange( index, BUY ) ;
        }
    }
    
    protected abstract boolean isBuyZoneActivated( int index ) ;
    
    protected abstract boolean isSellZoneActivated( int index ) ;
    
    protected abstract boolean isBuyConditionMet( int index ) ;
    
    protected abstract boolean isSellConditionMet( int index ) ;
    
}
