package com.sandy.capitalyst.algofoundry.app.bt.gui.panel.sim;

import com.sandy.capitalyst.algofoundry.app.core.ui.SwingUtils;
import com.sandy.capitalyst.algofoundry.app.core.ui.UITheme;

import javax.swing.*;
import java.awt.*;

public class SimControlPanel extends JPanel {
    
    public static class SimControlWidget extends JPanel {
        protected final SimPanel simPanel ;
        protected SimControlWidget( SimPanel simPanel ) {
            this.simPanel = simPanel ;
        }
    }
    
    private final PlayCtrlWidget playCtrlWidget ;
    private final LogDisplayWidget logDisplayWidget ;
    private final TradeBookWidget tradeBookWidget ;
    
    SimControlPanel( SimPanel simPanel ) {
        setPreferredSize( new Dimension( 300, 100 ) ) ;
        setOpaque( true ) ;
        setBackground( UITheme.BACKGROUND_COLOR ) ;
        
        this.playCtrlWidget = new PlayCtrlWidget( simPanel ) ;
        this.logDisplayWidget = new LogDisplayWidget( simPanel ) ;
        this.tradeBookWidget = new TradeBookWidget( simPanel ) ;
        
        setUpUI() ;
    }
    
    private void setUpUI() {
        SwingUtils.initPanelUI( this ) ;
        add( this.logDisplayWidget, BorderLayout.CENTER ) ;
        add( this.playCtrlWidget, BorderLayout.SOUTH ) ;
        add( this.tradeBookWidget, BorderLayout.NORTH ) ;
    }
    
    public LogDisplayWidget getLogDisplayWidget() {
        return this.logDisplayWidget ;
    }

    public TradeBookWidget getTradeBookWidget() {
        return this.tradeBookWidget ;
    }
}
