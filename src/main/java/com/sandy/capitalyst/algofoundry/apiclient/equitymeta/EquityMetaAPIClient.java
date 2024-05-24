package com.sandy.capitalyst.algofoundry.apiclient.equitymeta;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandy.capitalyst.algofoundry.core.offline.Offline;
import com.sandy.capitalyst.algofoundry.core.util.CapitalystServerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.* ;

@Slf4j
@Component
public class EquityMetaAPIClient {
    
    private static final String RECO_URL = "http://{server}/Equity/Recommendations" ;

    @Offline
    public List<EquityMeta> getEquityMetaList() throws Exception {
        
        List<EquityMeta> metaList = new ArrayList<>() ;
        String           recoJson = CapitalystServerUtil.getResource( RECO_URL ) ;
        
        ObjectMapper objMapper = new ObjectMapper() ;
        objMapper.disable( FAIL_ON_UNKNOWN_PROPERTIES ) ;
        
        JsonNode jsonRoot  = objMapper.readTree( recoJson ) ;
        Iterator<JsonNode> recoNodes = jsonRoot.elements() ;
        
        while( recoNodes.hasNext() ) {
            JsonNode   recoNode = recoNodes.next() ;
            EquityMeta meta     = new EquityMeta() ;
            
            metaList.add( populate( recoNode, meta, objMapper ) ) ;
        }
        return metaList ;
    }
    
    private EquityMeta populate( JsonNode node, EquityMeta meta, ObjectMapper mapper )
        throws IOException  {
        
        JsonNode em = node.get( "equityMaster" ) ;
        JsonNode indicators = node.get( "indicators" ) ;
        JsonNode ttmPerf = node.get( "ttmPerf" ) ;
        
        mapper.readerForUpdating( meta ).readValue( em ) ;
        mapper.readerForUpdating( meta ).readValue( indicators ) ;
        mapper.readerForUpdating( meta ).readValue( ttmPerf ) ;
        
        return meta ;
    }
}
