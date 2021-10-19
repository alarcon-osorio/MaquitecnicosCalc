package com.server.calc.repository;

import com.server.calc.entity.DataProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryDataProduct extends JpaRepository<DataProduct, Long> {
    @Query(value = "select * from dataproduct d where d.id =?1", nativeQuery = true)
    DataProduct findId(Long id);
}
