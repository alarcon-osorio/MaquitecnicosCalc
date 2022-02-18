package com.server.calc.repository;

import com.server.calc.entity.DataTemp;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface RepositoryDataTemp extends CrudRepository<DataTemp, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO datatemp (imports, ref, descript, cant, vip, distri, consu, pub)  VALUES (?, ?, ?, ?, ?, ?, ?, ?)", nativeQuery = true)
    void saveData(@Param("imports") String imports,
                  @Param("ref")String ref,
                  @Param("desc")String descript,
                  @Param("cant")long cant,
                  @Param("vip")String vip,
                  @Param("distri")String distri,
                  @Param("consu")String consu,
                  @Param("pub")String pub);

}
