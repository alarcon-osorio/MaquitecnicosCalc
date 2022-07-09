package com.server.calc.repository;

import com.server.calc.entity.DataClients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryDataClients extends JpaRepository<DataClients, Long> {

    @Query(value = "SELECT * FROM maquitecnicoscalc.dataclients WHERE nit = ?1 ",
            nativeQuery = true)
    DataClients findByNit(String nit);

}
