package com.sandy.capitalyst.algofoundry.ui;

import com.sandy.capitalyst.algofoundry.core.ui.SwingUtils;
import com.sandy.capitalyst.algofoundry.core.ui.UITheme;
import com.sandy.capitalyst.algofoundry.ui.panel.EquityMetaTablePanel;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

import static com.sandy.capitalyst.algofoundry.core.ui.UITheme.* ;

@Slf4j
public class AlgoFoundryFrame extends JFrame {
    
    private JTabbedPane          tabbedPane ;
    private EquityMetaTablePanel recoPanel ;

    public AlgoFoundryFrame() {
        super() ;

        setUpUI() ;
        setVisible( true ) ;
    }
    
    private void setUpUI() {
        
        setResizable( false ) ;
        setTitle( "Capitalyst Algo Foundry" ) ;
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ) ;
        
        Container contentPane = super.getContentPane() ;
        
        contentPane.setBackground( BACKGROUND_COLOR ) ;
        contentPane.setLayout( new BorderLayout() ) ;

        tabbedPane = new JTabbedPane( JTabbedPane.LEFT ) ;
        recoPanel = new EquityMetaTablePanel() ;
        tabbedPane.add( "Stock List", recoPanel ) ;

        contentPane.add( tabbedPane ) ;
        
        SwingUtils.setMaximized( this ) ;
    }
}
