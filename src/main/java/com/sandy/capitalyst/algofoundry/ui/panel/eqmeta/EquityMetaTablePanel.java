package com.sandy.capitalyst.algofoundry.ui.panel.eqmeta;

import com.sandy.capitalyst.algofoundry.apiclient.equitymeta.EquityMeta;
import com.sandy.capitalyst.algofoundry.apiclient.equitymeta.EquityMetaAPIClient;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Slf4j
public class EquityMetaTablePanel extends JPanel {
    
    private EquityMetaTable table ;
    
    public EquityMetaTablePanel() {
        
        EquityMetaAPIClient apiClient = new EquityMetaAPIClient() ;
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
        
        final JScrollPane tableSP = new JScrollPane( this.table ) ;
        tableSP.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED ) ;
        
        add( tableSP, BorderLayout.CENTER ) ;
    }
}
