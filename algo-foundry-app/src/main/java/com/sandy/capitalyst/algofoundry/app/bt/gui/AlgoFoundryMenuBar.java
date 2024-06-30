package com.sandy.capitalyst.algofoundry.app.bt.gui;

import com.sandy.capitalyst.algofoundry.app.AlgoFoundry;
import com.sandy.capitalyst.algofoundry.app.core.AlgoFoundryConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class AlgoFoundryMenuBar extends JMenuBar {
    
    public static final Font SYMBOL_MI_FONT = new Font( "Helvetica", Font.PLAIN, 11 ) ;
    
    private final AlgoFoundryFrame frame ;
    
    private       JMenu                  windowsMenu ;
    private final Map<String, JMenuItem> symbolMenuItems = new HashMap<>() ;
    
    AlgoFoundryMenuBar( AlgoFoundryFrame frame ) {
        this.frame = frame ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        add( createFileMenu() ) ;
        add( createOptionsMenu() ) ;
        add( createWindowsMenu() ) ;
    }
    
    private JMenu createFileMenu() {
        JMenu menu = new JMenu( "File" ) ;
        menu.setMnemonic( KeyEvent.VK_F ) ;
        
        JMenuItem exitMI = new JMenuItem( "Exit", KeyEvent.VK_X ) ;
        KeyStroke exitMIAccl = KeyStroke.getKeyStroke( "meta pressed Q" ) ;
        exitMI.setAccelerator( exitMIAccl ) ;
        exitMI.addActionListener( e -> frame.exitApp() ) ;
        
        menu.add( exitMI ) ;
        
        return menu ;
    }
    
    private JMenu createOptionsMenu() {
        JMenu menu = new JMenu( "Options" ) ;
        menu.setMnemonic( KeyEvent.VK_O ) ;
        AlgoFoundryConfig config = AlgoFoundry.getConfig() ;
        
        JCheckBoxMenuItem offlineMI = new JCheckBoxMenuItem( "Offline enabled" ) ;
        offlineMI.setState( config.isWorkOffline() ) ;
        offlineMI.addChangeListener( e -> config.setWorkOffline( offlineMI.getState() ) );
        
        menu.add( offlineMI ) ;
        
        return menu ;
    }

    private JMenu createWindowsMenu() {
        windowsMenu = new JMenu( "Windows" ) ;
        windowsMenu.setMnemonic( KeyEvent.VK_W ) ;
        
        JMenuItem closeCurrentTabMI = new JMenuItem( "Close", KeyEvent.VK_C ) ;
        KeyStroke closeCurrentTabAccl = KeyStroke.getKeyStroke( "meta pressed W" ) ;
        closeCurrentTabMI.setAccelerator( closeCurrentTabAccl ) ;
        closeCurrentTabMI.addActionListener( e -> frame.closeCurrentTab() ) ;
        
        JMenuItem closeAllTabsMI = new JMenuItem( "Close All", KeyEvent.VK_A ) ;
        KeyStroke closeAllTabsAccl = KeyStroke.getKeyStroke( "meta shift pressed W" ) ;
        closeAllTabsMI.setAccelerator( closeAllTabsAccl ) ;
        closeAllTabsMI.addActionListener( e -> frame.closeAllTabs() ) ;
        
        windowsMenu.add( closeCurrentTabMI ) ;
        windowsMenu.add( closeAllTabsMI ) ;
        windowsMenu.addSeparator() ;
        
        return windowsMenu ;
    }
    
    void addSimPanelMenuItem( String symbol ) {
        JMenuItem simPanelMI = new JMenuItem( symbol ) ;
        simPanelMI.addActionListener( e -> frame.showSimPanel( symbol ) ) ;
        simPanelMI.setFont( SYMBOL_MI_FONT ) ;
        symbolMenuItems.put( symbol, simPanelMI ) ;
        
        windowsMenu.add( simPanelMI ) ;
        windowsMenu.revalidate() ;
    }

    void removeSimPanelMenuItem( String symbol ) {
        windowsMenu.remove( symbolMenuItems.get( symbol ) ) ;
    }
}
