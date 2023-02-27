package com.server.calc.repository;

import com.server.calc.entity.DataStatic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RepositoryDataStatic extends JpaRepository<DataStatic, Long> {

    @Query(value = "select * from datastatic order by position asc", nativeQuery = true)
    List<DataStatic> findAllByPosition();

    @Query(value = "select * from datastatic d where d.id =?1", nativeQuery = true)
    DataStatic findId(Long id);
}
