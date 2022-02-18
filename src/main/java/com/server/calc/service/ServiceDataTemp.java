package com.server.calc.service;

import com.server.calc.entity.DataTemp;
import com.server.calc.repository.RepositoryDataTemp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceDataTemp {

    @Autowired
    RepositoryDataTemp repositoryDataTemp;

      public void saveDataTemp(long id, String imports,
                                 String ref,
                                 String desc,
                                 String cant,
                                 String vip,
                                 String distri,
                                 String consu,
                                 String pub,
                                 String export){
          repositoryDataTemp.saveData(id, imports, ref, desc, cant, vip, distri, consu, pub, export);
    }

}
