package com.sandy.capitalyst.algofoundry.app.equity.repo;

import com.sandy.capitalyst.algofoundry.app.equity.EquityMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EquityMasterRepo extends
        CrudRepository<EquityMaster, Integer>,
        JpaRepository<EquityMaster, Integer> {
    
    public EquityMaster findBySymbol( String symbol ) ;
    
    @Query( value =
            "SELECT em.symbol "
            + "FROM EquityMaster em "
            + "ORDER BY "
            + "   em.symbol ASC "
    )
    List<String> findNseSymbols() ;
}
