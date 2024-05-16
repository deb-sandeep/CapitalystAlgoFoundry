package com.sandy.capitalyst.algofoundry.ui.panel.sim;

import com.sandy.capitalyst.algofoundry.core.ui.UITheme;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import static com.sandy.capitalyst.algofoundry.core.ui.SwingUtils.* ;

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
    private static final int MAX_EMIT_DELAY = 1000 ;
    
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

    private final JComboBox<String> buyRuleComboBox = new JComboBox<>() ;
    private final JComboBox<String> sellRuleComboBox = new JComboBox<>() ;
    
    private final ImageIcon playIcon ;
    private final ImageIcon pauseIcon ;
    
    private PlayState playState = PlayState.YET_TO_START;
    private int emitDelayMs = 25 ;
    
    private Thread playDaemon ;
    
    PlayCtrlWidget( SimPanel simPanel ) {
        super( simPanel ) ;
        
        this.playIcon = getIcon( "control_play" ) ;
        this.pauseIcon = getIcon( "control_pause" ) ;
        
        this.playPauseBtn = createButton( "control_play", this ) ;
        this.restartBtn = createButton( "control_restart", this ) ;
        this.stepBtn = createButton( "control_step", this ) ;
        this.stopBtn = createButton( "control_stop", this ) ;
        
        this.restartBtn.setEnabled( false ) ;
        
        this.emitDelaySlider = new JSlider( JSlider.HORIZONTAL,
                                            MIN_EMIT_DELAY, MAX_EMIT_DELAY,
                                            this.emitDelayMs ) ;
        
        setUpRuleSelectors() ;
        setUpUI() ;
        
        this.playDaemon = new PlayDaemonThread() ;
        this.playDaemon.start() ;
    }
    
    private void setUpRuleSelectors() {
        
        setUpComboBoxUI( buyRuleComboBox ) ;
        setUpComboBoxUI( sellRuleComboBox ) ;
        
        simPanel.getBuyRuleNames().forEach( buyRuleComboBox::addItem ) ;
        simPanel.getSellRuleNames().forEach( sellRuleComboBox::addItem ) ;
        
        setUpRuleSelectionListeners() ;
    }
    
    private void setUpComboBoxUI( JComboBox<String> comboBox ) {
        comboBox.setEditable( false ) ;
        comboBox.setRenderer( new RuleSelectionRenderer() ) ;
    }
    
    private void setUpRuleSelectionListeners() {
        buyRuleComboBox.addActionListener( (e) -> {
            simPanel.setBuyRule( (String)buyRuleComboBox.getSelectedItem() ) ;
        } ) ;
        
        sellRuleComboBox.addActionListener( (e) -> {
            simPanel.setSellRule( (String)sellRuleComboBox.getSelectedItem() ) ;
        } ) ;
    }
    
    private void setUpUI() {
        initPanelUI( this ) ;
        
        add( getRuleSelectionPanel(), BorderLayout.NORTH ) ;
        add( getPlayPauseBtnPanel(), BorderLayout.CENTER ) ;
        add( getEmitDelayChangeSlider(), BorderLayout.SOUTH ) ;
        
        refreshControls() ;
    }
    
    private JPanel getRuleSelectionPanel() {
        JPanel panel = getNewJPanel() ;
        panel.setLayout( new GridLayout( 2, 1 ) ) ;
        panel.add( getBuyRulePanel() ) ;
        panel.add( getSellRulePanel() ) ;
        return panel ;
    }
    
    private JPanel getBuyRulePanel() {
        TitledBorder border = BorderFactory.createTitledBorder( "Buy rule" ) ;
        border.setTitleColor( Color.GRAY.brighter() ) ;
        
        JPanel panel = getNewJPanel() ;
        panel.setBorder( border ) ;
        panel.add( buyRuleComboBox ) ;
        return panel ;
    }
    
    private JPanel getSellRulePanel() {
        TitledBorder border = BorderFactory.createTitledBorder( "Sell rule" ) ;
        border.setTitleColor( Color.GRAY.brighter() ) ;

        JPanel panel = getNewJPanel() ;
        panel.setBorder( border ) ;
        panel.add( sellRuleComboBox ) ;
        return panel ;
    }
    
    private JPanel getPlayPauseBtnPanel() {
        JPanel panel = getNewJPanel() ;
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
        
        JPanel panel = getNewJPanel() ;
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
        
        buyRuleComboBox.setEnabled( enableStates[4] );
        sellRuleComboBox.setEnabled( enableStates[4] );
        
        playPauseBtn.setIcon( ( playState == PlayState.PLAYING ) ?
                              pauseIcon : playIcon ) ;
    }
    
    @Override
    public void stateChanged( ChangeEvent e ) {
        if( !emitDelaySlider.getValueIsAdjusting() ) {
            this.emitDelayMs = emitDelaySlider.getValue() ;
            if( this.emitDelayMs == 0 ) {
                this.emitDelayMs = 2 ;
            }
        }
    }
}
