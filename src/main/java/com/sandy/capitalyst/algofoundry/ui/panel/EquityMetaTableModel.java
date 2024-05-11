package com.sandy.capitalyst.algofoundry.ui.panel;

import com.sandy.capitalyst.algofoundry.apiclient.equitymeta.EquityMeta;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.AbstractTableModel;
import java.util.List;

@Slf4j
public class EquityMetaTableModel extends AbstractTableModel {
    
    @Data
    public static class Range52W implements Comparable<Range52W> {
        
        private final float low ;
        private final float high ;
        private final float val ;
        
        Range52W( float low, float high, float val ) {
            this.low = low ;
            this.high = high ;
            this.val = val ;
        }
        
        private float getPctHighFrom52WLow() {
            return ((val-low)/(high-low))*100 ;
        }
        
        @Override
        public int compareTo( @NotNull Range52W o ) {
            return (int)(getPctHighFrom52WLow() - o.getPctHighFrom52WLow()) ;
        }
    }
    
    /**
     * The column properties for this table. Each row contains a row description
     * where the 0th column is the column header and the 1st column is the
     * class of object representing the type of object representing the column
     * data.
     */
    public static final Object[][] COL_PROPERTIES = {
            { "Symbol",   String.class },
            { "Name",     String.class },
            { "Price",    Double.class },
            { "52W Range",Range52W.class },
            { "1D %",     Double.class },
            { "1W %",     Double.class },
            { "2W %",     Double.class },
            { "1M %",     Double.class },
            { "2M %",     Double.class },
            { "3M %",     Double.class },
            { "4M %",     Double.class },
            { "5M %",     Double.class },
            { "6M %",     Double.class },
            { "7M %",     Double.class },
            { "8M %",     Double.class },
            { "9M %",     Double.class },
            { "12M %",    Double.class }
    } ;
    
    public static final int COL_SYMBOL   = 0 ;
    public static final int COL_NAME     = 1 ;
    public static final int COL_PRICE    = 2 ;
    public static final int COL_52W_RANGE= 3 ;
    public static final int COL_PERF_1D  = 4 ;
    public static final int COL_PERF_1W  = COL_PERF_1D + 1 ;
    public static final int COL_PERF_2W  = COL_PERF_1D + 2 ;
    public static final int COL_PERF_1M  = COL_PERF_1D + 3 ;
    public static final int COL_PERF_2M  = COL_PERF_1D + 4 ;
    public static final int COL_PERF_3M  = COL_PERF_1D + 5 ;
    public static final int COL_PERF_4M  = COL_PERF_1D + 6 ;
    public static final int COL_PERF_5M  = COL_PERF_1D + 7 ;
    public static final int COL_PERF_6M  = COL_PERF_1D + 8 ;
    public static final int COL_PERF_7M  = COL_PERF_1D + 9 ;
    public static final int COL_PERF_8M  = COL_PERF_1D + 10 ;
    public static final int COL_PERF_9M  = COL_PERF_1D + 11 ;
    public static final int COL_PERF_12M = COL_PERF_1D + 12 ;
    
    private final List<EquityMeta> equityMetaList;
    
    @Getter private float minTTMPerf = 0 ;
    @Getter private float maxTTMPerf = 0 ;
    
    public EquityMetaTableModel( List<EquityMeta> metaList ) {
        this.equityMetaList = metaList ;
        computeTTMPerfRange() ;
    }
    
    @Override
    public int getColumnCount() {
        return COL_PROPERTIES.length ;
    }
    
    @Override
    public int getRowCount() {
        return this.equityMetaList.size() ;
    }
    
    @Override
    public String getColumnName( final int column ) {
        return ( String )COL_PROPERTIES[column][0] ;
    }
    
    @Override
    public Class<?> getColumnClass( final int columnIndex ) {
        return ( Class<?> )COL_PROPERTIES[columnIndex][1] ;
    }
    
    public EquityMeta getEquityMetaForRow( final int row ) {
        return this.equityMetaList.get( row ) ;
    }
    
    /**
     * Returns the value of the column at the specified row index.
     */
    @Override
    public Object getValueAt( final int rowIndex, final int columnIndex ) {
        final EquityMeta m = this.equityMetaList.get( rowIndex ) ;
        return switch( columnIndex ) {
            case COL_SYMBOL    -> m.getSymbol();
            case COL_NAME      -> m.getName();
            case COL_PRICE     -> m.getCurrentPrice();
            case COL_52W_RANGE -> new Range52W( m.getLow52(), m.getHigh52(), m.getCurrentPrice() ) ;
            case COL_PERF_1D   -> m.getPerf1d() ;
            case COL_PERF_1W   -> m.getPerf1w() ;
            case COL_PERF_2W   -> m.getPerf2w() ;
            case COL_PERF_1M   -> m.getPerf1m() ;
            case COL_PERF_2M   -> m.getPerf2m() ;
            case COL_PERF_3M   -> m.getPerf3m() ;
            case COL_PERF_4M   -> m.getPerf4m() ;
            case COL_PERF_5M   -> m.getPerf5m() ;
            case COL_PERF_6M   -> m.getPerf6m() ;
            case COL_PERF_7M   -> m.getPerf7m() ;
            case COL_PERF_8M   -> m.getPerf8m() ;
            case COL_PERF_9M   -> m.getPerf9m() ;
            case COL_PERF_12M  -> m.getPerf12m() ;
            default -> null;
        } ;
    }
    
    private void computeTTMPerfRange() {
        for( int col=COL_PERF_1D; col<=COL_PERF_12M; col++ ) {
            for( int row=0; row<this.equityMetaList.size(); row++ ) {
                float val = (Float)getValueAt( row, col ) ;
                this.minTTMPerf = Math.min( this.minTTMPerf, val ) ;
                this.maxTTMPerf = Math.max( this.maxTTMPerf, val ) ;
            }
        }
    }
}
