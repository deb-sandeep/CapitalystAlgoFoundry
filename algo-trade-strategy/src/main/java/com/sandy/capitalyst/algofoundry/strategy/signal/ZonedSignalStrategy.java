package com.sandy.capitalyst.algofoundry.strategy.signal;

import com.sandy.capitalyst.algofoundry.strategy.StrategyConfig;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.CurrentSignalZoneEvent;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.TradeSignalEvent;

import static com.sandy.capitalyst.algofoundry.strategy.signal.event.CurrentSignalZoneEvent.* ;
import static com.sandy.capitalyst.algofoundry.strategy.signal.event.CurrentSignalZoneEvent.MovementType.* ;
import static com.sandy.capitalyst.algofoundry.strategy.signal.event.CurrentSignalZoneEvent.ZoneType.* ;

public abstract class ZonedSignalStrategy extends SignalStrategy {
    
    private final int activeZoneMaxAge;
    
    private int blackoutDaysLeft ;
    private int activeEntryDaysLeft = 0 ;
    private int activeExitDaysLeft  = 0 ;
    
    protected ZonedSignalStrategy( CandleSeries history, StrategyConfig config ) {
        super( history, config ) ;
        this.blackoutDaysLeft = config.getInitialBlackoutNumDays() ;
        this.activeZoneMaxAge = config.getActiveZoneMaxAge() ;
    }
    
    public void clear() {
        super.clear() ;
        this.blackoutDaysLeft = config.getInitialBlackoutNumDays() ;
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
            return activeZoneMaxAge - activeEntryDaysLeft + 1 ;
        }
        return 0 ;
    }
    
    protected int getNumDaysIntoExitZone() {
        if( isInSellActivePeriod() ) {
            return activeZoneMaxAge - activeExitDaysLeft + 1 ;
        }
        return 0 ;
    }
    
    private void pubCurrentZone( ZoneType type ) {
        super.publishEvent( new CurrentSignalZoneEvent( date, bar, type, CURRENT ) ) ;
    }
    
    private void pubBuyZoneEntry() {
        super.publishEvent( new CurrentSignalZoneEvent( date, bar, BUY, ENTRY ) ) ;
        activeEntryDaysLeft = activeZoneMaxAge ;
        activeExitDaysLeft = 0 ;
    }
    
    private void pubSellZoneEntry() {
        super.publishEvent( new CurrentSignalZoneEvent( date, bar, SELL, ENTRY ) ) ;
        activeEntryDaysLeft = 0 ;
        activeExitDaysLeft = activeZoneMaxAge ;
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
