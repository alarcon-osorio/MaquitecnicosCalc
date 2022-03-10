package com.server.calc.repository;

import com.server.calc.entity.DataUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryDataUsers extends JpaRepository<DataUsers, Long> {

    @Query(value = "select * from datausers where email = ?1", nativeQuery = true)
    DataUsers findByEmail(String email);

}
