package com.sandy.capitalyst.algofoundry.app.backtester.gui;

import com.sandy.capitalyst.algofoundry.app.AlgoFoundry;
import com.sandy.capitalyst.algofoundry.app.backtester.gui.panel.eqmeta.EquityMetaTablePanel;
import com.sandy.capitalyst.algofoundry.app.backtester.gui.panel.sim.SimPanel;
import com.sandy.capitalyst.algofoundry.app.backtester.gui.tuner.HyperparameterTunerFrame;
import com.sandy.capitalyst.algofoundry.app.core.bus.Event;
import com.sandy.capitalyst.algofoundry.app.core.bus.EventSubscriber;
import com.sandy.capitalyst.algofoundry.app.core.ui.SwingUtils;
import com.sandy.capitalyst.algofoundry.app.core.ui.UITheme;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static com.sandy.capitalyst.algofoundry.app.EventCatalog.EVT_SHOW_STOCK_SIM_PANEL;

@Slf4j
public class AlgoFoundryFrame extends JFrame implements EventSubscriber {
    
    private JTabbedPane          tabbedPane ;
    private AlgoFoundryMenuBar   menuBar ;
    private EquityMetaTablePanel recoPanel ;
    
    private final Map<String, SimPanel> simPanels = new HashMap<>() ;
    
    private HyperparameterTunerFrame tunerFrame = null ;

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
        
        contentPane.setBackground( UITheme.BACKGROUND_COLOR ) ;
        contentPane.setLayout( new BorderLayout() ) ;

        tabbedPane = new JTabbedPane( JTabbedPane.LEFT ) ;
        tabbedPane.setBackground( UITheme.BACKGROUND_COLOR ) ;
        tabbedPane.addChangeListener( e -> {
            super.setTitle( tabbedPane.getTitleAt( tabbedPane.getSelectedIndex() ) ) ;
        } ) ;
        
        recoPanel = new EquityMetaTablePanel() ;
        
        tabbedPane.addTab( "Stock List", recoPanel ) ;

        contentPane.add( tabbedPane ) ;
        
        menuBar = new AlgoFoundryMenuBar( this ) ;
        super.setJMenuBar( menuBar ) ;
        
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
                    menuBar.addSimPanelMenuItem( symbol ) ;
                }
                tabbedPane.setSelectedComponent( simPanel ) ;
            }
            catch( Exception e ) {
                JOptionPane.showMessageDialog( this, "Error creating sim panel" ) ;
                log.error( "Error creating sim panel for {}", event.getValue(), e ) ;
            }
        }
    }
    
    public void closeAllTabs() {
        for( int i=tabbedPane.getTabCount()-1; i>0; i-- ) {
            this.removeTabAt( i ) ;
        }
        tabbedPane.setSelectedIndex( tabbedPane.getTabCount()-1 ) ;
    }
    
    public void closeCurrentTab() {
        int selectedTabIndex = tabbedPane.getSelectedIndex() ;
        JPanel panel = ( JPanel )tabbedPane.getComponentAt( selectedTabIndex ) ;
        if( panel instanceof SimPanel ) {
            this.removeTabAt( selectedTabIndex ) ;
            if( tabbedPane.getTabCount() > 0 ) {
                tabbedPane.setSelectedIndex( tabbedPane.getTabCount()-1 ) ;
            }
        }
    }
    
    private void removeTabAt( int index ) {
        SimPanel simPanel = ( SimPanel )tabbedPane.getComponentAt( index ) ;
        String symbol = simPanel.getSymbol() ;
        menuBar.removeSimPanelMenuItem( symbol ) ;
        tabbedPane.removeTabAt( index ) ;
        simPanels.remove( symbol ) ;
    }

    public void exitApp() {
        this.dispose() ;
        System.exit( -1 ) ;
    }
    
    public void showSimPanel( String symbol ) {
        tabbedPane.setSelectedComponent( simPanels.get( symbol ) ) ;
    }
    
    public void showTunerFrame() {
        try {
            if( tunerFrame == null || !tunerFrame.isDisplayable() ) {
                tunerFrame = new HyperparameterTunerFrame( recoPanel.getMetaList() ) ;
            }
            else {
                tunerFrame.toFront() ;
            }
        }
        catch( Exception e ) {
            log.error( "Could not create tuner frame.", e ) ;
        }
    }
}
