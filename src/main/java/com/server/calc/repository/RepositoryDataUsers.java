package com.server.calc.repository;

import com.server.calc.entity.DataUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryDataUsers extends JpaRepository<DataUsers, Long> {
    @Query(value = "select * from datausers", nativeQuery = true)
    DataUsers findByEmail(String email);
}
