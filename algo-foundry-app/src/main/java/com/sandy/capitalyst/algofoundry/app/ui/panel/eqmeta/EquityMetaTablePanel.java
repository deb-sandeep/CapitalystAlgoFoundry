package com.sandy.capitalyst.algofoundry.app.ui.panel.eqmeta;

import com.sandy.capitalyst.algofoundry.app.apiclient.equitymeta.EquityMeta;
import com.sandy.capitalyst.algofoundry.app.apiclient.equitymeta.EquityMetaAPIClient;
import com.sandy.capitalyst.algofoundry.app.apiclient.histeod.EquityHistEODAPIClient;
import com.sandy.capitalyst.algofoundry.app.core.ui.UITheme;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.app.AlgoFoundry.* ;

@Slf4j
public class EquityMetaTablePanel extends JPanel {
    
    private EquityMetaTable table ;
    
    public EquityMetaTablePanel() {
        
        EquityMetaAPIClient    apiClient  = getBean( EquityMetaAPIClient.class ) ;
        EquityHistEODAPIClient histClient = getBean( EquityHistEODAPIClient.class ) ;
        
        List<EquityMeta> metaList ;
        
        try{
            log.debug( "Creating reco table panel" ) ;
            metaList = apiClient.getEquityMetaList() ;
            
            log.debug( "Fetched meta list. {} records.", metaList.size() );
            this.table = new EquityMetaTable( metaList ) ;
            
            setUpUI() ;
        }
        catch( Exception e ) {
            log.error( "Could not fetch Equity Meta list.", e ) ;
        }
    }
    
    private void setUpUI() {
        setLayout( new BorderLayout() ) ;
        setBackground( UITheme.BACKGROUND_COLOR ) ;
        
        final JScrollPane tableSP = new JScrollPane( this.table ) ;
        tableSP.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED ) ;
        
        add( tableSP, BorderLayout.CENTER ) ;
    }
}