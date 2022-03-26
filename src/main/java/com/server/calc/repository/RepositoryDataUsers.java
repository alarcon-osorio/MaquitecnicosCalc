package com.server.calc.repository;

import com.server.calc.dto.UsersDTO;
import com.server.calc.entity.DataUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface RepositoryDataUsers extends JpaRepository<DataUsers, Long> {

    @Query(value = "select * from datausers where email = ?1", nativeQuery = true)
    DataUsers findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "insert into datausers (name, email, login, password, profile, state, authid) values (?, ?, ?, ?, ?, ?, ?);", nativeQuery = true)
    void saveIdem(String name, String email, String login, String password, String profile, String state, String authid);


}
