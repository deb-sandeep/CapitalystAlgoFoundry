package com.sandy.capitalyst.algofoundry.ui.panel.sim;

import com.sandy.capitalyst.algofoundry.core.ui.UITheme;

import javax.swing.*;
import java.awt.*;

import static com.sandy.capitalyst.algofoundry.core.ui.SwingUtils.initPanelUI;

public class SimControlPanel extends JPanel {
    
    public static class SimControlWidget extends JPanel {
        protected final SimPanel simPanel ;
        protected SimControlWidget( SimPanel simPanel ) {
            this.simPanel = simPanel ;
        }
    }
    
    private final PlayCtrlWidget playCtrlWidget ;
    private final LogDisplayWidget logDisplayWidget ;
    
    SimControlPanel( SimPanel simPanel ) {
        setPreferredSize( new Dimension( 300, 100 ) ) ;
        setOpaque( true ) ;
        setBackground( UITheme.BACKGROUND_COLOR ) ;
        
        this.playCtrlWidget = new PlayCtrlWidget( simPanel ) ;
        this.logDisplayWidget = new LogDisplayWidget( simPanel ) ;
        
        setUpUI() ;
    }
    
    private void setUpUI() {
        initPanelUI( this ) ;
        add( this.logDisplayWidget, BorderLayout.CENTER ) ;
        add( this.playCtrlWidget, BorderLayout.SOUTH ) ;
    }
    
    public LogDisplayWidget getLogDisplayWidget() {
        return this.logDisplayWidget ;
    }
}
