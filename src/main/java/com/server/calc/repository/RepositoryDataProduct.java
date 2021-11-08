package com.server.calc.repository;

import com.server.calc.entity.DataProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryDataProduct extends JpaRepository<DataProduct, Long> {
    @Query(value = "select * from dataproduct d where d.id =?1", nativeQuery = true)
    DataProduct findId(Long id);

    @Query(value = "select * from dataproduct d where d.reference =?1", nativeQuery = true)
    List<DataProduct> findByReferenceList(String reference);

    @Query(value = "select * from dataproduct d where d.reference =?1", nativeQuery = true)
    DataProduct findByReference(String reference);

    @Query(value = "select * FROM `dataproduct` WHERE reference = ?1 and amount = ?2", nativeQuery = true)
    DataProduct findByValueDollar(String reference, long amount);

}
