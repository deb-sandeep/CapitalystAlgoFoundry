package com.sandy.capitalyst.algofoundry.ui.panel.sim;

import com.sandy.capitalyst.algofoundry.core.ui.UITheme;

import javax.swing.*;
import java.awt.*;

public class SimControlPanel extends JPanel {
    
    private final SimPanel simPanel ;
    
    SimControlPanel( SimPanel simPanel ) {
        this.simPanel = simPanel ;
        setPreferredSize( new Dimension( 300, 100 ) ) ;
        setOpaque( true ) ;
        setBackground( UITheme.BACKGROUND_COLOR ) ;
    }
}
