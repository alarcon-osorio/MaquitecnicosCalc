package com.server.calc.repository;

import com.server.calc.entity.DataFeatures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryDataFestures extends JpaRepository<DataFeatures, Long> {
    @Query(value = "SELECT * " +
            "FROM datafeatures " +
            "WHERE module = ?1 " +
            "AND menunumber = ?2 " +
            "AND keymenu = ?3",
            nativeQuery = true)
    List<DataFeatures> findDataFeatures(String module, long menunumber, String keymenu);

    @Query(value = "SELECT DISTINCT menu FROM datafeatures where not menunumber = 4",
            nativeQuery = true)
    List<String> findDistincMenu();

    @Query(value = "SELECT DISTINCT keymenu FROM datafeatures where not keymenu = 'ini-end'",
            nativeQuery = true)
    List<String> findDistinctKeyMenu();

    @Query(value = "SELECT DISTINCT menunumber FROM datafeatures where menu = ?1",
            nativeQuery = true)
    Long findDataFeaturesMenuNumber(String menu);

    @Query(value = "SELECT DISTINCT module FROM datafeatures where menu = ?1",
            nativeQuery = true)
    String findDataFeaturesModule(String menu);

}
