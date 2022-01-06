package com.server.calc.repository;

import com.server.calc.entity.DataStatic;
import com.server.calc.entity.DataType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RepositoryDataType extends JpaRepository<DataType, Long> {

    @Query(value = "select * from datatype d where d.id =?1", nativeQuery = true)
    DataType findId(Long importId);

}
