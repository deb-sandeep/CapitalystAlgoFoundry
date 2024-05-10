package com.sandy.capitalyst.algofoundry.core.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class SwingUtils {

    public static void setMaximized( JFrame frame ) {
        Dimension screenSz = Toolkit.getDefaultToolkit().getScreenSize() ; 
        frame.setBounds( 0, 0, screenSz.width, screenSz.height ) ;
    }
    
    public static void centerOnScreen( Component component, int width, int height ) {
        
        int x = getScreenWidth()/2 - width/2 ;
        int y = getScreenHeight()/2 - height/2 ;
        
        component.setBounds( x, y, width, height ) ;
    }
    
    public static int getScreenWidth() {
        Dimension screenSz = Toolkit.getDefaultToolkit().getScreenSize() ; 
        return screenSz.width ;
    }
    
    public static int getScreenHeight() {
        Dimension screenSz = Toolkit.getDefaultToolkit().getScreenSize() ; 
        return screenSz.height ;
    }
    
    public static ImageIcon getIcon( String iconName ) {
        URL url = SwingUtils.class.getResource( "/icons/" + iconName + ".png" ) ;
        Image image = Toolkit.getDefaultToolkit().getImage( url ) ;
        return new ImageIcon( image ) ;
    }
    
    public static BufferedImage getIconImage( String iconName ) {
        BufferedImage img ;
        try {
            URL url = SwingUtils.class.getResource( "/icons/" + iconName + ".png" ) ;
            assert url != null;
            img = ImageIO.read( url ) ;
        }
        catch( IOException e ) {
            throw new RuntimeException( e ) ;
        }
        return img ;
    }
}
