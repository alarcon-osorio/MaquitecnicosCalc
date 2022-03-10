package com.server.calc.repository;

import com.server.calc.dto.DataCalcJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryDataTrm extends JpaRepository<DataCalcJoin, Long> {
    @Query(value = "SELECT t1.id, " +
            "t1.iddatastatic, " +
            "t2.concept, " +
            "t1.value_cop, " +
            "t1.legalization " +
            "FROM datacalc t1 " +
            "left join datastatic t2 on t1.iddatastatic = t2.id", nativeQuery = true)
    List<DataCalcJoin> findDataCalcJoin();

    @Query(value = "SELECT t1.id, " +
            "t1.iddatastatic, " +
            "t2.concept, " +
            "t1.value_cop, " +
            "t1.legalization " +
            "FROM datacalc t1 " +
            "left join datastatic t2 on t1.iddatastatic = t2.id " +
            "where t1.id = ?1", nativeQuery = true)
    DataCalcJoin findDataCalcJoinById(long id);

}
