package com.sandy.capitalyst.algofoundry.ui.panel.sim;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.sandy.capitalyst.algofoundry.core.ui.SwingUtils.* ;

@Slf4j
public class PlayCtrlWidget extends SimControlPanel.SimControlWidget
    implements ActionListener, ChangeListener {
    
    private enum PlayState { YET_TO_START, PLAYING, PAUSED, ENDED } ;
    private static final int MIN_EMIT_DELAY = 0 ;
    private static final int MAX_EMIT_DELAY = 1000 ;

    private JButton playPauseBtn ;
    private JButton stepBtn ;
    private JButton restartBtn ;
    private JSlider emitDelaySlider ;
    
    private ImageIcon playIcon ;
    private ImageIcon pauseIcon ;
    
    private PlayState playState = PlayState.YET_TO_START;
    private int emitDelayMs = 250 ;
    
    private Thread playDaemon = new Thread( () -> {
        while( true ) {
            try {
                if( playState == PlayState.PLAYING ) {
                    if( !simPanel.playCurrentBarSeriesData() ) {
                        playState = PlayState.ENDED ;
                        refreshButtons() ;
                    }
                }
                Thread.sleep( emitDelayMs ) ;
            }
            catch( InterruptedException e ) {
                throw new RuntimeException( e );
            }
        }
    } ) ;
    
    PlayCtrlWidget( SimPanel simPanel ) {
        super( simPanel ) ;
        
        this.playIcon = getIcon( "control_play" ) ;
        this.pauseIcon = getIcon( "control_pause" ) ;
        
        this.playPauseBtn = createButton( "control_play", this ) ;
        this.restartBtn = createButton( "control_restart", this ) ;
        this.stepBtn = createButton( "control_step", this ) ;
        
        this.restartBtn.setEnabled( false ) ;
        
        this.emitDelaySlider = new JSlider( JSlider.HORIZONTAL,
                                            MIN_EMIT_DELAY, MAX_EMIT_DELAY,
                                            this.emitDelayMs ) ;
        setUpUI() ;
        this.playDaemon.start() ;
    }
    
    private void setUpUI() {
        initPanelUI( this ) ;
        
        setLayout( new GridLayout( 2, 1 ) ) ;
        add( getPlayPauseBtnPanel() ) ;
        add( getEmitDelayChangeSlider() ) ;
        
        refreshButtons() ;
    }
    
    private JPanel getPlayPauseBtnPanel() {
        JPanel panel = getNewJPanel() ;
        panel.setLayout( new GridLayout( 1, 3 ) ) ;
        panel.add( this.playPauseBtn ) ;
        panel.add( this.stepBtn ) ;
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
                playState = PlayState.PLAYING ;
            }
            else if( playState == PlayState.PLAYING ) {
                playState = PlayState.PAUSED ;
            }
        }
        else if( src == restartBtn ) {
            simPanel.restartSimulation() ;
            playState = PlayState.YET_TO_START;
            refreshButtons() ;
        }
        else if( src == stepBtn ) {
            if( !simPanel.playCurrentBarSeriesData() ) {
                playState = PlayState.ENDED ;
            }
        }
        refreshButtons() ;
    }
    
    private void refreshButtons() {
        if( playState == PlayState.YET_TO_START ) {
            this.restartBtn.setEnabled( false ) ;
            this.stepBtn.setEnabled( false ) ;
            this.playPauseBtn.setEnabled( true ) ;
            
            this.playPauseBtn.setIcon( this.playIcon ) ;
        }
        else if( playState == PlayState.PLAYING ) {
            this.restartBtn.setEnabled( false ) ;
            this.stepBtn.setEnabled( false ) ;
            this.playPauseBtn.setEnabled( true ) ;
            
            this.playPauseBtn.setIcon( this.pauseIcon ) ;
        }
        else if( playState == PlayState.PAUSED ) {
            this.restartBtn.setEnabled( false ) ;
            this.stepBtn.setEnabled( true ) ;
            this.playPauseBtn.setEnabled( true ) ;
            
            this.playPauseBtn.setIcon( this.playIcon ) ;
        }
        else if( playState == PlayState.ENDED ) {
            this.restartBtn.setEnabled( true ) ;
            this.stepBtn.setEnabled( false ) ;
            this.playPauseBtn.setEnabled( false ) ;
        }
    }
    
    @Override
    public void stateChanged( ChangeEvent e ) {
        if( !emitDelaySlider.getValueIsAdjusting() ) {
            this.emitDelayMs = emitDelaySlider.getValue() ;
            if( this.emitDelayMs == 0 ) {
                this.emitDelayMs = 5 ;
            }
        }
    }
}
