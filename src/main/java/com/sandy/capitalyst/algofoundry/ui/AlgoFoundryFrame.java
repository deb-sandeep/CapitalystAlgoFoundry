package com.sandy.capitalyst.algofoundry.ui;

import com.sandy.capitalyst.algofoundry.AlgoFoundry;
import com.sandy.capitalyst.algofoundry.core.bus.Event;
import com.sandy.capitalyst.algofoundry.core.bus.EventSubscriber;
import com.sandy.capitalyst.algofoundry.core.ui.SwingUtils;
import com.sandy.capitalyst.algofoundry.ui.panel.eqmeta.EquityMetaTablePanel;
import com.sandy.capitalyst.algofoundry.ui.panel.sim.SimPanel;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static com.sandy.capitalyst.algofoundry.EventCatalog.EVT_SHOW_STOCK_SIM_PANEL;
import static com.sandy.capitalyst.algofoundry.core.ui.UITheme.BACKGROUND_COLOR;

@Slf4j
public class AlgoFoundryFrame extends JFrame implements EventSubscriber {
    
    private JTabbedPane          tabbedPane ;
    private EquityMetaTablePanel recoPanel ;
    
    private final Map<String, SimPanel> simPanels = new HashMap<>() ;

    public AlgoFoundryFrame() {
        super() ;

        setUpUI() ;
        setVisible( true ) ;
        
        AlgoFoundry.getBus()
                   .addSubscriberForEventTypes( this, true, EVT_SHOW_STOCK_SIM_PANEL ) ;
    }
    
    private void setUpUI() {
        
        setResizable( true ) ;
        setTitle( "Capitalyst Algo Foundry" ) ;
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ) ;
        
        Container contentPane = super.getContentPane() ;
        
        contentPane.setBackground( BACKGROUND_COLOR ) ;
        contentPane.setLayout( new BorderLayout() ) ;

        tabbedPane = new JTabbedPane( JTabbedPane.LEFT ) ;
        tabbedPane.setBackground( BACKGROUND_COLOR ) ;
        
        recoPanel = new EquityMetaTablePanel() ;
        
        tabbedPane.addTab( "Stock List", recoPanel ) ;

        contentPane.add( tabbedPane ) ;
        
        SwingUtils.setMaximized( this ) ;
    }
    
    @Override
    public void handleEvent( Event event ) {
        if( event.getEventType() == EVT_SHOW_STOCK_SIM_PANEL ) {
            try {
                String symbol = (String)event.getValue() ;
                SimPanel simPanel = simPanels.get( symbol ) ;
                if( simPanel == null ) {
                    simPanel = new SimPanel( symbol ) ;
                    simPanels.put( symbol, simPanel ) ;
                    tabbedPane.add( symbol, simPanel ) ;
                }
                tabbedPane.setSelectedComponent( simPanel ) ;
            }
            catch( Exception e ) {
                JOptionPane.showMessageDialog( this, "Error creating sim panel" ) ;
                log.error( "Error creating sim panel for {}", event.getValue(), e ) ;
            }
        }
    }
}
