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
    @Query(value = "INSERT INTO datatemp (id, imports, ref, desc, cant, vip, distri, consu, pub, export)  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", nativeQuery = true)
    void saveData(@Param("id") long id,
                  @Param("imports") String imports,
                  @Param("ref")String ref,
                  @Param("desc")String desc,
                  @Param("cant")String cant,
                  @Param("vip")String vip,
                  @Param("distri")String distri,
                  @Param("consu")String consu,
                  @Param("pub")String pub,
                  @Param("export")String export);

}
