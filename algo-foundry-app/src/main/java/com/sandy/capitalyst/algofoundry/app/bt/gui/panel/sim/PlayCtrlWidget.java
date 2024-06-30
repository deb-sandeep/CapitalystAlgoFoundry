package com.sandy.capitalyst.algofoundry.app.bt.gui.panel.sim;

import com.sandy.capitalyst.algofoundry.app.core.ui.SwingUtils;
import com.sandy.capitalyst.algofoundry.app.core.ui.UITheme;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PlayCtrlWidget extends SimControlPanel.SimControlWidget
    implements ActionListener, ChangeListener {
    
    public static class RuleSelectionRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent( JList<?> list,
                                                       Object value,
                                                       int index,
                                                       boolean isSelected,
                                                       boolean cellHasFocus ) {
            
            JLabel label = (JLabel)super.getListCellRendererComponent(
                              list, value, index, isSelected, cellHasFocus ) ;
            label.setPreferredSize( new Dimension( 100, 25 ) );
            label.setBackground( UITheme.BACKGROUND_COLOR.brighter() ) ;
            label.setForeground( Color.GRAY.brighter() ) ;
            
            if( isSelected ) {
                label.setForeground( Color.WHITE ) ;
            }
            
            return label ;
        }
    }
    
    private class PlayDaemonThread extends Thread {
        public void run() {
            while( playState != PlayState.ENDED ) {
                try {
                    if( playState == PlayState.PLAYING ) {
                        if( !simPanel.playCurrentBarSeriesData() ) {
                            playState = PlayState.ENDED ;
                            simPanel.simulationEnded() ;
                            refreshControls() ;
                        }
                    }
                    Thread.sleep( emitDelayMs ) ;
                }
                catch( InterruptedException e ) {
                    throw new RuntimeException( e );
                }
            }
        }
    }
    
    private enum PlayState { YET_TO_START, PLAYING, PAUSED, ENDED }
    private static final int MIN_EMIT_DELAY = 0 ;
    private static final int MAX_EMIT_DELAY = 500 ;
    
    private static final Map<PlayState, boolean[]> BTN_ENABLE_STATES = new HashMap<>() ;
    static {
        // Boolean flags are in the order play, step, stop, reset, ruleSelection
        BTN_ENABLE_STATES.put( PlayState.YET_TO_START, new boolean[]{ true,  false, false, false, true  } ) ;
        BTN_ENABLE_STATES.put( PlayState.PLAYING,      new boolean[]{ true,  false, true,  false, false } ) ;
        BTN_ENABLE_STATES.put( PlayState.PAUSED,       new boolean[]{ true,  true,  true,  false, false } ) ;
        BTN_ENABLE_STATES.put( PlayState.ENDED,        new boolean[]{ false, false, false, true,  true  } ) ;
    }
    
    private final JButton playPauseBtn ;
    private final JButton stepBtn ;
    private final JButton stopBtn ;
    private final JButton restartBtn ;
    private final JSlider emitDelaySlider ;

    private final JComboBox<String> strategyComboBox = new JComboBox<>() ;
    
    private final ImageIcon playIcon ;
    private final ImageIcon pauseIcon ;
    
    private PlayState playState = PlayState.YET_TO_START;
    private int emitDelayMs = 5 ;
    
    private Thread playDaemon ;
    
    PlayCtrlWidget( SimPanel simPanel ) {
        super( simPanel ) ;
        
        this.playIcon = SwingUtils.getIcon( "control_play" ) ;
        this.pauseIcon = SwingUtils.getIcon( "control_pause" ) ;
        
        this.playPauseBtn = SwingUtils.createButton( "control_play", this ) ;
        this.restartBtn = SwingUtils.createButton( "control_restart", this ) ;
        this.stepBtn = SwingUtils.createButton( "control_step", this ) ;
        this.stopBtn = SwingUtils.createButton( "control_stop", this ) ;
        
        this.restartBtn.setEnabled( false ) ;
        
        this.emitDelaySlider = new JSlider( JSlider.HORIZONTAL,
                                            MIN_EMIT_DELAY, MAX_EMIT_DELAY,
                                            this.emitDelayMs ) ;
        
        setUpTradeStrategySelector() ;
        setUpUI() ;
        setUpButtonKeyListeners() ;
        
        this.playDaemon = new PlayDaemonThread() ;
        this.playDaemon.start() ;
    }
    
    private void setUpButtonKeyListeners() {
        this.stepBtn.addKeyListener( new KeyAdapter() {
            @Override
            public void keyTyped( KeyEvent e ) {
                if( e.getKeyChar() == 'n' ) {
                    if( stepBtn.isEnabled() ) {
                        ActionEvent ae = new ActionEvent( stepBtn, 0, null ) ;
                        actionPerformed( ae ) ;
                    }
                }
            }
        } );
    }
    
    private void setUpTradeStrategySelector() {
        
        strategyComboBox.setEditable( false ) ;
        strategyComboBox.setRenderer( new RuleSelectionRenderer() ) ;

        simPanel.getTradeStrategyNames().forEach( strategyComboBox::addItem ) ;
        
        strategyComboBox.addActionListener( (e) ->
                simPanel.setTradeStrategy( (String)strategyComboBox.getSelectedItem() ) ) ;
    }
    
    private void setUpUI() {
        SwingUtils.initPanelUI( this ) ;
        
        add( getRuleSelectionPanel(), BorderLayout.NORTH ) ;
        add( getPlayPauseBtnPanel(), BorderLayout.CENTER ) ;
        add( getEmitDelayChangeSlider(), BorderLayout.SOUTH ) ;
        
        refreshControls() ;
    }
    
    private JPanel getRuleSelectionPanel() {
        TitledBorder border = BorderFactory.createTitledBorder( "Trade Strategy" ) ;
        border.setTitleColor( Color.GRAY.brighter() ) ;
        
        JPanel panel = SwingUtils.getNewJPanel() ;
        panel.setBorder( border ) ;
        panel.add( strategyComboBox ) ;
        return panel ;
    }
    
    private JPanel getPlayPauseBtnPanel() {
        JPanel panel = SwingUtils.getNewJPanel() ;
        panel.setLayout( new GridLayout( 1, 4 ) ) ;
        panel.add( this.playPauseBtn ) ;
        panel.add( this.stepBtn ) ;
        panel.add( this.stopBtn ) ;
        panel.add( this.restartBtn ) ;
        return panel ;
    }
    
    private JPanel getEmitDelayChangeSlider() {
        emitDelaySlider.setMajorTickSpacing(500);
        emitDelaySlider.setMinorTickSpacing(100);
        emitDelaySlider.setPaintTicks(true);
        emitDelaySlider.setPaintLabels(true);
        emitDelaySlider.setForeground( Color.LIGHT_GRAY );
        
        emitDelaySlider.addChangeListener( this ) ;
        
        JPanel panel = SwingUtils.getNewJPanel() ;
        panel.add( emitDelaySlider, BorderLayout.CENTER ) ;
        return panel ;
    }
    
    @Override
    public void actionPerformed( ActionEvent e ) {
        
        JButton src = (JButton)e.getSource() ;
        if( src == playPauseBtn ) {
            if( playState == PlayState.YET_TO_START ||
                playState == PlayState.PAUSED ) {
                
                if( playState == PlayState.YET_TO_START ) {
                    simPanel.setTradeStrategy( (String)strategyComboBox.getSelectedItem() );
                    simPanel.doPrePlayProcessing() ;
                }
                playState = PlayState.PLAYING ;
            }
            else if( playState == PlayState.PLAYING ) {
                playState = PlayState.PAUSED ;
            }
        }
        else if( src == restartBtn ) {
            simPanel.restartSimulation() ;
            playState = PlayState.YET_TO_START;
            playDaemon = new PlayDaemonThread() ;
            playDaemon.start() ;
            refreshControls() ;
        }
        else if( src == stepBtn ) {
            if( !simPanel.playCurrentBarSeriesData() ) {
                playState = PlayState.ENDED ;
            }
        }
        else if( src == stopBtn ) {
            playState = PlayState.ENDED ;
        }
        refreshControls() ;
    }
    
    private void refreshControls() {
        
        boolean[] enableStates = BTN_ENABLE_STATES.get( playState ) ;
        playPauseBtn.setEnabled( enableStates[0] ) ;
        stepBtn.setEnabled     ( enableStates[1] ) ;
        stopBtn.setEnabled     ( enableStates[2] ) ;
        restartBtn.setEnabled  ( enableStates[3] ) ;
        
        strategyComboBox.setEnabled( enableStates[4] );
        
        playPauseBtn.setIcon( ( playState == PlayState.PLAYING ) ?
                              pauseIcon : playIcon ) ;
    }
    
    @Override
    public void stateChanged( ChangeEvent e ) {
        if( !emitDelaySlider.getValueIsAdjusting() ) {
            this.emitDelayMs = emitDelaySlider.getValue() ;
        }
    }
}
