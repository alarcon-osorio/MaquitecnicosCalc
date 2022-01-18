package com.server.calc.repository;

import com.server.calc.entity.DataCalc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryDataCalc extends JpaRepository<DataCalc, Long> {
    @Query(value = "select * from datacalc d where d.iddatastatic = ?1", nativeQuery = true)
    DataCalc findBydDataStaticId(long id);

}


