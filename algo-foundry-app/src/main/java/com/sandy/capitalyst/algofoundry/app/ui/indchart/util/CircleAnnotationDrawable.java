package com.sandy.capitalyst.algofoundry.app.ui.indchart.util;

import com.sandy.capitalyst.algofoundry.app.core.ui.UITheme;
import org.jfree.ui.Drawable;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class CircleAnnotationDrawable implements Drawable {
    
    private final Paint   paint;
    private final boolean fill ;
    
    public CircleAnnotationDrawable( Paint color ) {
        this( color, true ) ;
    }
    
    public CircleAnnotationDrawable( Paint color, boolean fill ) {
        this.paint = color ;
        this.fill = fill ;
    }
    
    public void draw( final Graphics2D g2, final Rectangle2D area ) {
        final Ellipse2D circle = new Ellipse2D.Double(
                                        area.getX(), area.getY(),
                                        area.getWidth(), area.getHeight());
        g2.setPaint( paint ) ;
        g2.setStroke( UITheme.LINE_STROKE_1_5 ) ;
        
        if( fill ) {
            g2.fill( circle ) ;
        }
        else {
            g2.draw( circle ) ;
        }
    }
}
