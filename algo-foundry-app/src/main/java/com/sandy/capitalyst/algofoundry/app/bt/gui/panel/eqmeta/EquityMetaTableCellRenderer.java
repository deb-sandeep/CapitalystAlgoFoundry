package com.sandy.capitalyst.algofoundry.app.bt.gui.panel.eqmeta;

import com.sandy.capitalyst.algofoundry.app.core.ui.UITheme;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

import static com.sandy.capitalyst.algofoundry.app.bt.gui.panel.eqmeta.EquityMetaTableModel.* ;

@Slf4j
public class EquityMetaTableCellRenderer extends DefaultTableCellRenderer {
    
    /** The decimal format used to render price values. */
    protected static final DecimalFormat PRICE_DF = new DecimalFormat( "###0 " ) ;
    protected static final DecimalFormat PCT_DF   = new DecimalFormat( "###0.00 " ) ;
    
    private EquityMetaTableModel tableModel = null ;
    
    public EquityMetaTableCellRenderer( final EquityMetaTableModel model ) {
        super() ;
        this.tableModel = model ;
    }
    
    @Override
    public Component getTableCellRendererComponent(
            final JTable table, final Object value, final boolean isSelected,
            final boolean hasFocus, final int row, final int column ) {
        
        final int modelCol = table.convertColumnIndexToModel( column ) ;
        final int modelRow = table.convertRowIndexToModel( row ) ;
        
        if( modelCol == COL_52W_RANGE ) {
            return new RangeLabel52W( (Range52W)value ) ;
        }
        
        // Let the super class have the first stab at rendering the cell.
        final JLabel label = ( JLabel )super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column ) ;
        
        // Do the basic rendering.
        label.setText( "" ) ;
        label.setOpaque( true ) ;
        label.setBackground( Color.WHITE ) ;
        label.setBorder( BorderFactory.createEmptyBorder() ) ;
        
        setBackgroundColor( isSelected, row, label, modelRow, modelCol ) ;
        
        setLabelText( value, label, modelRow, modelCol ) ;
        setLabelAlignment( label, modelCol ) ;
        setColorGradation( value, label, modelCol, modelRow ) ;
        
        return label ;
    }
    
    private void setColorGradation( final Object value, final JLabel label,
                                    final int modelCol, final int modelRow ) {
    }
    
    private void setBackgroundColor( final boolean isSelected, final int row,
                                     final JLabel label,
                                     final int modelRow, final int modelCol ) {
        if( isSelected ) {
            label.setBackground( Color.CYAN ) ;
            label.setForeground( Color.BLACK ) ;
        }
        else if( row % 2 == 0 ) {
            label.setBackground( UITheme.TABLE_EVEN_ROW_COLOR ) ;
        }
        else {
            label.setBackground( UITheme.TABLE_ODD_ROW_COLOR ) ;
        }

        if( modelCol >= COL_PERF_1D && modelCol <= COL_PERF_12M ) {
            label.setBackground( computeTTMPerfColor( modelRow, modelCol ) );
        }
    }
    
    private Color computeTTMPerfColor( int modelRow, int modelCol ) {
        
        float val = ( float )tableModel.getValueAt( modelRow, modelCol ) ;
        if( val < 0 ) {
            float minTTFPerf = tableModel.getMinTTMPerf() ;
            return Color.getHSBColor( 1.0f, sigmoidX( minTTFPerf, val ), 1.0f ) ;
        }
        else {
            float maxTTMPerf = tableModel.getMaxTTMPerf() ;
            return Color.getHSBColor( 0.4f, sigmoidX( maxTTMPerf, val ), 1.0f ) ;
        }
    }
    
    private float sigmoidX( double max, double value ) {
        double eVal = Math.exp( (2.5/Math.abs( max ))*Math.abs( value ) ) ;
        return (float)((2*( (eVal/(eVal+1))-0.5 ))+0.05) ;
    }
    
    private void setLabelAlignment( final JLabel label, final int modelCol ) {
        if( modelCol == COL_NAME ||
            modelCol == COL_SYMBOL ) {
            label.setHorizontalAlignment( JLabel.LEFT ) ;
            label.setHorizontalTextPosition( JLabel.LEFT ) ;
        }
        else {
            label.setHorizontalAlignment( JLabel.RIGHT ) ;
            label.setHorizontalTextPosition( JLabel.RIGHT ) ;
        }
    }
    
    private void setLabelText( final Object value, final JLabel label,
                               final int modelRow, final int modelCol ) {
        
        if( value instanceof Double ||
            value instanceof Float ) {
            
            label.setFont( UITheme.TABLE_DECIMAL_FONT ) ;
            if( modelCol == COL_PRICE ||
                modelCol == COL_MKT_CAP ) {
                label.setText( PRICE_DF.format( value ) ) ;
            }
            else if( modelCol >= COL_PERF_1D && modelCol <= COL_PERF_12M ) {
                label.setText( PCT_DF.format( value ) ) ;
            }
            else if( modelCol == COL_SWING ) {
                label.setText( String.valueOf( value ) ) ;
            }
        }
        else if( value instanceof String ) {
            if( modelCol == COL_SYMBOL ) {
                label.setFont( UITheme.META_TABLE_HDR_FONT ) ;
            }
            label.setText( (String)value ) ;
        }
    }
}
