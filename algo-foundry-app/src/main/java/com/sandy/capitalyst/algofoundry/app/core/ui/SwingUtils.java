package com.sandy.capitalyst.algofoundry.app.core.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
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
    
    public static JButton createButton( String iconName, ActionListener actionListener ) {
        return createButton( null, iconName, actionListener ) ;
    }
    
    public static JButton createButton( String label, String iconName, ActionListener actionListener ) {
        
        JButton btn = new JButton() ;
        if( label != null ) {
            btn.setText( label ) ;
        }
        if( iconName != null ) {
            btn.setIcon( getIcon( iconName ) ) ;
            if( label == null ) {
                btn.setOpaque( true ) ;
                btn.setBackground( UITheme.BACKGROUND_COLOR ) ;
                btn.setBorderPainted( false ) ;
            }
        }
        if( actionListener != null ) {
            btn.addActionListener( actionListener ) ;
        }
        
        return btn ;
    }
    
    public static JPanel getNewJPanel() {
        JPanel panel = new JPanel() ;
        initPanelUI( panel ) ;
        return panel ;
    }

    public static void initPanelUI( JPanel panel ) {
        panel.setOpaque( true ) ;
        panel.setBackground( UITheme.BACKGROUND_COLOR ) ;
        panel.setLayout( new BorderLayout() ) ;
    }
    
    public static JLabel getNewJLabel( String text ) {
        JLabel label = new JLabel( text ) ;
        label.setBackground( UITheme.BACKGROUND_COLOR ) ;
        label.setForeground( Color.LIGHT_GRAY ) ;
        return label ;
    }
}
