package com.server.calc.repository;

import com.server.calc.entity.DataFeatures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryDataFestures extends JpaRepository<DataFeatures, Long> {
}
