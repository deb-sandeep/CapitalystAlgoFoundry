package com.sandy.capitalyst.algofoundry.ui.indchart.util;

import org.jfree.ui.Drawable;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class CircleAnnotationDrawable implements Drawable {
    
    private Paint fillPaint;
    
    public CircleAnnotationDrawable( final Paint fillPaint ) {
        this.fillPaint = fillPaint ;
    }
    
    public void draw( final Graphics2D g2, final Rectangle2D area ) {
        final Ellipse2D circle = new Ellipse2D.Double(
                                        area.getX(), area.getY(),
                                        area.getWidth(), area.getHeight());
        g2.setPaint( fillPaint ) ;
        g2.fill( circle ) ;
    }
}
