package com.sandy.capitalyst.algofoundry.ui.panel;

import com.sandy.capitalyst.algofoundry.apiclient.equitymeta.EquityMeta;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.ui.panel.EquityMetaTableModel.* ;
import static com.sandy.capitalyst.algofoundry.core.ui.UITheme.* ;

@Slf4j
public class EquityMetaTable extends JTable {
    
    /**
     * This anonymous internal class implements the logic of handling double
     * clicks on any row of the table. When a user double-clicks on any row,
     * it is a que for us to open up a simulation panel of that scrip.
     */
    private final MouseListener mouseListener = new MouseAdapter() {
        
        @Override
        public void mouseClicked( final MouseEvent e ) {
            
            if( e.getClickCount() == 2 ) {
                final int row = EquityMetaTable.this.rowAtPoint( e.getPoint() ) ;
                if( row != -1 ) {
                    final int                  modelRowId = convertRowIndexToModel( row ) ;
                    final EquityMetaTableModel model      = ( EquityMetaTableModel )getModel() ;
                    final EquityMeta           equityMeta = model.getEquityMetaForRow( modelRowId ) ;
                    
                    log.debug( "Table row for " + equityMeta.getSymbol() + " clicked." ) ;
                }
            }
        }
    } ;
    
    private final EquityMetaTableModel model ;
    
    public EquityMetaTable( List<EquityMeta> metaList ) {
        super() ;
        super.addMouseListener( this.mouseListener ) ;
        
        this.model = new EquityMetaTableModel( metaList ) ;
        
        setModel( this.model ) ;
        setGridColor( TABLE_GRID_COLOR ) ;
        setRowSelectionAllowed( true ) ;
        setSelectionMode( ListSelectionModel.SINGLE_SELECTION ) ;
        setColumnSelectionAllowed( false ) ;
        setRowHeight( TABLE_ROW_HEIGHT ) ;
        setFont( META_TABLE_FONT ) ;
        getTableHeader().setFont( META_TABLE_HDR_FONT ) ;
        setDoubleBuffered( true ) ;
        setAutoCreateRowSorter( true ) ;
        
        setColumnProperties( COL_SYMBOL,    100 ) ;
        setColumnProperties( COL_NAME,      350 ) ;
        setColumnProperties( COL_PRICE,      70 ) ;
        setColumnProperties( COL_52W_RANGE, 100 ) ;
        for( int col=COL_PERF_1D; col<=COL_PERF_12M; col++ ) {
            setColumnProperties( col, 70 ) ;
        }
        
        setDefaultRenderer( String.class,   new EquityMetaTableCellRenderer( this.model ) ) ;
        setDefaultRenderer( Double.class,   new EquityMetaTableCellRenderer( this.model ) ) ;
        setDefaultRenderer( Range52W.class, new EquityMetaTableCellRenderer( this.model ) ) ;
    }
    
    private void setColumnProperties( final int colId, final int width ) {
        final TableColumnModel colModel = getColumnModel() ;
        final TableColumn      col      = colModel.getColumn( colId ) ;
        
        col.setPreferredWidth( width ) ;
        col.setMinWidth( width ) ;
        col.setMaxWidth( width ) ;
        col.setResizable( true ) ;
    }
}
