package com.server.calc.repository;

import com.server.calc.entity.DataUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryDataUsers extends JpaRepository<DataUsers, Long> {

    @Query(value = "select * from datausers", nativeQuery = true)
    DataUsers findByEmail(String email);

    @Query(value = "select * from datausers", nativeQuery = true)
    List<DataUsers> findAllUsers();
}
