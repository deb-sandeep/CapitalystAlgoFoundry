package com.sandy.capitalyst.algofoundry.app.equity.repo;

import com.sandy.capitalyst.algofoundry.app.equity.HistoricEQData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface HistoricEQDataRepo 
    extends CrudRepository<HistoricEQData, Integer>,
            JpaRepository<HistoricEQData, Integer> {
    
    @Query( value =
            "SELECT h "
          + "FROM HistoricEQData h "
          + "WHERE "
          + "   h.symbol = :symbol AND "
          + "   h.date < :beforeDate "
          + "ORDER BY "
          + "   h.date ASC "
    )
    List<HistoricEQData> getHistoricData( @Param( "symbol"   ) String symbol,
                                          @Param( "beforeDate" ) Date beforeDate ) ;
}
