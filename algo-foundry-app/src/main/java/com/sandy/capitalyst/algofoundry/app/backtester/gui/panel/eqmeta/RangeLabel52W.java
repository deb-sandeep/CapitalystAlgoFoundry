package com.sandy.capitalyst.algofoundry.app.backtester.gui.panel.eqmeta;

import javax.swing.*;
import java.awt.*;

public class RangeLabel52W extends JLabel {
    
    private final EquityMetaTableModel.Range52W rangeData ;
    
    public RangeLabel52W( EquityMetaTableModel.Range52W range ) {
        this.rangeData = range ;
    }
    
    public void paint( final Graphics g ) {
        
        final int height    = getSize().height ;
        final int width     = getSize().width ;
        final float min     = rangeData.getLow() ;
        final float max     = rangeData.getHigh() ;
        final float val     = rangeData.getVal() ;
        
        final Graphics2D g2d = ( Graphics2D )g ;
        g2d.setColor( Color.lightGray ) ;
        g2d.drawLine( 7, (height/2), width-15, (height/2) );

        g2d.setColor( Color.red ) ;
        g2d.fillOval( 5, (height/2)-2, 5, 5 ) ;
        
        g2d.setColor( Color.blue ) ;
        g2d.fillOval( width-15, (height/2)-2, 5, 5 ) ;
        
        int rangeLen = width-13-7 ;
        int valPos   = (int)((((rangeLen)*(val-min))/(max-min)) + 7) ;
        
        g2d.setColor( Color.gray ) ;
        g2d.fillRect( valPos-1, 4, 3, height-8 ) ;
        
    }
}
