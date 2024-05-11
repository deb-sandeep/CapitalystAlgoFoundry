package com.sandy.capitalyst.algofoundry.ui.panel;

import com.sandy.capitalyst.algofoundry.apiclient.equitymeta.EquityMeta;
import lombok.extern.slf4j.Slf4j;

import javax.swing.table.AbstractTableModel;
import java.util.List;

@Slf4j
public class EquityMetaTableModel extends AbstractTableModel {
    
    /**
     * The column properties for this table. Each row contains a row description
     * where the 0th column is the column header and the 1st column is the
     * class of object representing the type of object representing the column
     * data.
     */
    public static final Object[][] COL_PROPERTIES = {
            { "Symbol",   String.class },
            { "Name",     String.class },
            { "Price",    Double.class }
    } ;
    
    public static final int COL_SYMBOL   = 0 ;
    public static final int COL_NAME     = 1 ;
    public static final int COL_PRICE    = 2 ;
    
    private final List<EquityMeta> equityMetaList;
    
    public EquityMetaTableModel( List<EquityMeta> metaList ) {
        this.equityMetaList = metaList ;
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
        final EquityMeta meta = this.equityMetaList.get( rowIndex ) ;
        return switch( columnIndex ) {
            case COL_SYMBOL    -> meta.getSymbol();
            case COL_NAME      -> meta.getName();
            case COL_PRICE     -> meta.getCurrentPrice();
            default -> null;
        } ;
    }
}
