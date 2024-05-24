package com.sandy.capitalyst.algofoundry.ui.panel.sim;

import com.sandy.capitalyst.algofoundry.core.ui.UITheme;
import com.sandy.capitalyst.algofoundry.strategy.StrategyEvent;
import com.sandy.capitalyst.algofoundry.strategy.StrategyEventListener;
import com.sandy.capitalyst.algofoundry.strategy.event.CurrentZoneEvent;
import com.sandy.capitalyst.algofoundry.strategy.event.LogEvent;
import com.sandy.capitalyst.algofoundry.strategy.event.TradeEvent;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static com.sandy.capitalyst.algofoundry.core.ui.SwingUtils.* ;
import static com.sandy.capitalyst.algofoundry.core.util.StringUtil.fmtDate;
import static com.sandy.capitalyst.algofoundry.strategy.event.CurrentZoneEvent.MovementType.* ;
import static com.sandy.capitalyst.algofoundry.strategy.event.LogEvent.Level.*;
import static com.sandy.capitalyst.algofoundry.strategy.event.LogEvent.getIndent;

@Slf4j
public class LogDisplayWidget extends SimControlPanel.SimControlWidget
    implements StrategyEventListener {
    
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
        
        JScrollPane sp = new JScrollPane( textArea ) ;
        sp.setBorder( BorderFactory.createLineBorder( Color.GRAY.darker(), 1 ) ) ;
        sp.getVerticalScrollBar().setUI( new ScrollBarUI() ) ;
        sp.getHorizontalScrollBar().setUI( new ScrollBarUI() ) ;
        sp.setDoubleBuffered( true ) ;
        add( sp, BorderLayout.CENTER ) ;
    }
    
    @Override
    public void handleStrategyEvent( StrategyEvent event ) {
        
        String logMsg = null ;
        
        if( event instanceof CurrentZoneEvent ze ) {
            
            if( ze.getMovementType() != CURRENT ) {
                logMsg = getIndent( L2 ) + ">> " + ze.getZoneType() + " Zone Activated" ;
            }
            else {
                logMsg = "\n" + ( fmtDate( ze.getDate() ) + " : " +
                        ze.getZoneType() ) ;
            }
        }
        else if( event instanceof TradeEvent te ) {
            logMsg = getIndent( L2 ) + ">> " + te.getType() + " signal." ;
        }
        else if( event instanceof LogEvent evt ) {
            logMsg = getIndent( evt.getLevel() ) + evt.getMsg() ;
        }
        
        log( logMsg ) ;
    }
    
    private void log( String str ) {
        String newText = textArea.getText() + "\n" + str ;
        textArea.setText( newText ) ;
        textArea.setCaretPosition( newText.length() ) ;
    }
}
