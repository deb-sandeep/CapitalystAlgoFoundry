package com.sandy.capitalyst.algofoundry.dao.equity;

import com.sandy.capitalyst.algofoundry.apiclient.histeod.DayCandle;
import jakarta.persistence.*;
import lombok.Data;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.DecimalNum;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Data
@Entity
@Table( name = "historic_eq_data" )
public class HistoricEQData {

    @Id
    @TableGenerator(
        name            = "eodPkGen", 
        table           = "id_gen", 
        pkColumnName    = "gen_key", 
        valueColumnName = "gen_value", 
        pkColumnValue   = "historic_eq_data_id",
        initialValue    = 1,
        allocationSize  = 1 )    
    @GeneratedValue( 
        strategy=GenerationType.TABLE, 
        generator="eodPkGen" )
    private Integer id = null ;
    
    private String symbol = null ;
    private long   totalTradeQty = 0 ;
    private long   totalTrades = 0 ;
    private Date   date = null ;
    
    @Column( precision=16 )
    private float open = 0.0F ;
    
    @Column( precision=16 )
    private float high = 0.0F ;
    
    @Column( precision=16 )
    private float low = 0.0F ;
    
    @Column( precision=16 )
    private float close = 0.0F ;
    
    @Column( precision=16 )
    private Float prevClose = 0.0F ;
    
    @Column( precision=16 )
    private float totalTradeVal = 0.0F ;
    
    public DayCandle toDayCandle() {
        DayCandle candle = new DayCandle() ;
        candle.setDate  ( this.date  ) ;
        candle.setOpen  ( this.open  ) ;
        candle.setHigh  ( this.high  ) ;
        candle.setLow   ( this.low   ) ;
        candle.setClose ( this.close ) ;
        candle.setVolume( this.totalTradeQty ) ;
        return candle ;
    }
    
    public float getPctChange() {
        return ((close-prevClose)/prevClose)*100 ;
    }
}
