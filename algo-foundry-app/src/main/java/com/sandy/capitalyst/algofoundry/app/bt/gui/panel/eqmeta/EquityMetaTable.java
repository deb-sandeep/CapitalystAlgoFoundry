package com.sandy.capitalyst.algofoundry.app.bt.gui.panel.eqmeta;

import com.sandy.capitalyst.algofoundry.app.AlgoFoundry;
import com.sandy.capitalyst.algofoundry.app.apiclient.equitymeta.EquityMeta;
import com.sandy.capitalyst.algofoundry.app.core.ui.UITheme;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.app.bt.gui.panel.eqmeta.EquityMetaTableModel.* ;
import static com.sandy.capitalyst.algofoundry.app.EventCatalog.* ;

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
                    
                    AlgoFoundry.getBus()
                               .publishEvent( EVT_SHOW_STOCK_SIM_PANEL,
                                              equityMeta.getSymbol() );
                }
            }
        }
    } ;
    
    public EquityMetaTable( List<EquityMeta> metaList ) {
        super() ;
        super.addMouseListener( this.mouseListener ) ;
        
        EquityMetaTableModel model = new EquityMetaTableModel( metaList );
        
        setModel( model ) ;
        setGridColor( UITheme.TABLE_GRID_COLOR ) ;
        setRowSelectionAllowed( true ) ;
        setSelectionMode( ListSelectionModel.SINGLE_SELECTION ) ;
        setColumnSelectionAllowed( false ) ;
        setRowHeight( UITheme.TABLE_ROW_HEIGHT ) ;
        setFont( UITheme.META_TABLE_FONT ) ;
        getTableHeader().setFont( UITheme.META_TABLE_HDR_FONT ) ;
        setDoubleBuffered( true ) ;
        setAutoCreateRowSorter( true ) ;
        
        setColumnProperties( COL_SYMBOL,    100 ) ;
        setColumnProperties( COL_NAME,      350 ) ;
        setColumnProperties( COL_MKT_CAP,   100 ) ;
        setColumnProperties( COL_PRICE,      75 ) ;
        setColumnProperties( COL_52W_RANGE, 150 ) ;
        setColumnProperties( COL_SWING,      50 ) ;
        for( int col=COL_PERF_1D; col<=COL_PERF_12M; col++ ) {
            setColumnProperties( col, 65 ) ;
        }
        
        setDefaultRenderer( String.class,   new EquityMetaTableCellRenderer( model ) ) ;
        setDefaultRenderer( Double.class,   new EquityMetaTableCellRenderer( model ) ) ;
        setDefaultRenderer( Range52W.class, new EquityMetaTableCellRenderer( model ) ) ;
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
