package com.sandy.capitalyst.algofoundry.app.ui.panel.sim;

import com.sandy.capitalyst.algofoundry.app.core.ui.UITheme;
import com.sandy.capitalyst.algofoundry.strategy.signal.SignalStrategyEvent;
import com.sandy.capitalyst.algofoundry.strategy.signal.SignalStrategyEventListener;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.CurrentSignalZoneEvent;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.SignalStrategyLogEvent;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.TradeSignalEvent;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static com.sandy.capitalyst.algofoundry.app.core.ui.SwingUtils.initPanelUI;
import static com.sandy.capitalyst.algofoundry.app.core.util.StringUtil.fmtDate;
import static com.sandy.capitalyst.algofoundry.strategy.signal.event.CurrentSignalZoneEvent.MovementType.CURRENT;
import static com.sandy.capitalyst.algofoundry.strategy.signal.event.CurrentSignalZoneEvent.MovementType.ENTRY;
import static com.sandy.capitalyst.algofoundry.strategy.signal.event.SignalStrategyLogEvent.Level.L2;
import static com.sandy.capitalyst.algofoundry.strategy.signal.event.SignalStrategyLogEvent.getIndent;

@Slf4j
public class LogDisplayWidget extends SimControlPanel.SimControlWidget
    implements SignalStrategyEventListener {
    
    private static class ScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.trackColor = UITheme.BACKGROUND_COLOR ;
            this.thumbColor = UITheme.BACKGROUND_COLOR.brighter() ;
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }
        
        private JButton createZeroButton() {
            JButton jbutton = new JButton();
            jbutton.setPreferredSize(new Dimension(0, 0));
            jbutton.setMinimumSize(new Dimension(0, 0));
            jbutton.setMaximumSize(new Dimension(0, 0));
            return jbutton;
        }
    }
    
    private JTextArea textArea = null ;
    private JRadioButton enableButton = null ;
    private boolean enableLogs = false ;
    
    public LogDisplayWidget( SimPanel simPanel ) {
        super( simPanel ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        initPanelUI( this ) ;
        
        textArea = new JTextArea() ;
        textArea.setBackground( UITheme.BACKGROUND_COLOR ) ;
        textArea.setForeground( Color.LIGHT_GRAY.brighter() ) ;
        textArea.setFont( UITheme.LOG_UI_FONT ) ;
        textArea.setBorder( BorderFactory.createEmptyBorder() ) ;
        textArea.setEditable( false ) ;
        textArea.setDoubleBuffered( true ) ;
        textArea.addKeyListener( new KeyAdapter() {
            @Override
            public void keyTyped( KeyEvent e ) {
                if( e.getKeyChar() == 'd' ) {
                    textArea.setText( "" ) ;
                }
            }
        } ) ;
        
        enableButton = new JRadioButton( "Elable logs" ) ;
        enableButton.setForeground( Color.GRAY ) ;
        enableButton.addActionListener( e -> enableLogs = enableButton.isEnabled() ); ;
        
        JScrollPane sp = new JScrollPane( textArea ) ;
        sp.setBorder( BorderFactory.createLineBorder( Color.GRAY.darker(), 1 ) ) ;
        sp.getVerticalScrollBar().setUI( new ScrollBarUI() ) ;
        sp.getHorizontalScrollBar().setUI( new ScrollBarUI() ) ;
        sp.setDoubleBuffered( true ) ;
        
        add( sp, BorderLayout.CENTER ) ;
        add( enableButton, BorderLayout.SOUTH ) ;
    }
    
    @Override
    public void handleStrategyEvent( SignalStrategyEvent event ) {
        
        if( !enableLogs ) return ;
        
        String logMsg = null ;
        
        if( event instanceof CurrentSignalZoneEvent ze ) {
            
            if( ze.getMovementType() != CURRENT ) {
                logMsg = getIndent( L2 ) + ">> " + ze.getZoneType() + " Zone " +
                        (ze.getMovementType() == ENTRY ? "Activated" : "Deactivated") ;
            }
            else {
                if( ze.getZoneType() == CurrentSignalZoneEvent.ZoneType.BLACKOUT ) {
                    logMsg = ( fmtDate( ze.getDate() ) + " : " +
                             ze.getZoneType() ) ;
                }
                else {
                    logMsg = "\n" + ( fmtDate( ze.getDate() ) + " : " +
                             ze.getZoneType() + " Zone" ) ;
                }
            }
        }
        else if( event instanceof TradeSignalEvent te ) {
            logMsg = getIndent( L2 ) + ">> " + te.getType() + " signal." ;
        }
        else if( event instanceof SignalStrategyLogEvent evt ) {
            logMsg = getIndent( evt.getLevel() ) + evt.getMsg() ;
        }
        
        log( logMsg ) ;
    }
    
    private void log( String str ) {
        String newText = textArea.getText() + "\n" + str ;
        textArea.setText( newText ) ;
        textArea.setCaretPosition( newText.length() ) ;
    }
    
    public void clear() {
        textArea.setText( "" ) ;
    }
}
