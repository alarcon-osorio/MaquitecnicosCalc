package com.server.calc.repository;

import com.server.calc.entity.DataCalc;
import com.server.calc.entity.DataRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryDataRegistry extends JpaRepository<DataRegistry, Long> {

    @Query(value = "select  id, brand, cant, cantsug, client, cop, datereg, obs, reason, refoem, refsadt, user from dataregistry where datereg between ?1 and ?2", nativeQuery = true)
    List<DataRegistry> findRegistryByDate(String fechaIni, String fechaFin);

}
