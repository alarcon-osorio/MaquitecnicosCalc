package com.server.calc.repository;

import com.server.calc.entity.DataRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryDataRegistry extends JpaRepository<DataRegistry, Long> {
}
