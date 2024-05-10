package com.sandy.capitalyst.algofoundry.core.ui;

import com.sandy.capitalyst.algofoundry.core.AlgoFoundryConfig;
import com.sandy.capitalyst.algofoundry.core.ui.uiutil.SwingUtils;
import com.sandy.capitalyst.algofoundry.core.ui.uiutil.UITheme;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class AlgoFoundryFrame extends JFrame {
    
    private final Container contentPane ;

    public AlgoFoundryFrame( UITheme theme, AlgoFoundryConfig config ) {
        super() ;

        this.contentPane = super.getContentPane() ;

        setUpUI( theme ) ;
        setVisible( true ) ;
    }
    
    private void setUpUI( UITheme theme ) {
        
        setResizable( false ) ;
        setTitle( "Capitalyst Algo Foundry" ) ;
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ) ;
        
        contentPane.setBackground( theme.getBackgroundColor() ) ;
        contentPane.setLayout( new BorderLayout() ) ;

        JPanel panel = new JPanel() ;
        contentPane.add( panel ) ;

        SwingUtils.setMaximized( this ) ;
    }
}
