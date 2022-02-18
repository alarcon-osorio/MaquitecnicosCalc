package com.server.calc.repository;

import com.server.calc.entity.DataTemp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryDataTemp extends CrudRepository<DataTemp, Long> {
}
