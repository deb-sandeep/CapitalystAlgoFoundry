package com.sandy.capitalyst.algofoundry.apiclient.reco;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandy.capitalyst.algofoundry.core.util.CapitalystServerUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.* ;

@Slf4j
public class EquityRecoAPIClient {
    
    private static final String RECO_URL = "http://{server}/Equity/Recommendations" ;

    public List<EquityReco> getRecommendations() throws Exception {
        
        List<EquityReco> recos = new ArrayList<>() ;
        String recoJson = CapitalystServerUtil.getResource( RECO_URL ) ;
        
        ObjectMapper objMapper = new ObjectMapper() ;
        objMapper.disable( FAIL_ON_UNKNOWN_PROPERTIES ) ;
        
        JsonNode jsonRoot  = objMapper.readTree( recoJson ) ;
        Iterator<JsonNode> recoNodes = jsonRoot.elements() ;
        
        while( recoNodes.hasNext() ) {
            JsonNode recoNode = recoNodes.next() ;
            EquityReco reco = new EquityReco() ;
            
            recos.add( populate( recoNode, reco, objMapper ) ) ;
        }
        return recos ;
    }
    
    private EquityReco populate( JsonNode node, EquityReco reco, ObjectMapper mapper )
        throws IOException  {
        
        JsonNode em = node.get( "equityMaster" ) ;
        JsonNode indicators = node.get( "indicators" ) ;
        JsonNode ttmPerf = node.get( "ttmPerf" ) ;
        
        mapper.readerForUpdating( reco ).readValue( em ) ;
        mapper.readerForUpdating( reco ).readValue( indicators ) ;
        mapper.readerForUpdating( reco ).readValue( ttmPerf ) ;
        
        return reco ;
    }
}
