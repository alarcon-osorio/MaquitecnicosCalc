package com.server.calc.service;

import com.server.calc.entity.DataTemp;
import com.server.calc.repository.RepositoryDataTemp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceDataTemp {

    @Autowired
    RepositoryDataTemp repositoryDataTemp;

    public void saveDataTemp(  String imports,
                                 String ref,
                                 String descript,
                                 long cant,
                                 String vip,
                                 String distri,
                                 String consu,
                                 String pub){
        repositoryDataTemp.saveData(imports, ref, descript, cant, vip, distri, consu, pub);

    }

    public List<DataTemp> getDataTemp(){
        return (List<DataTemp>) repositoryDataTemp.findAll();
    }

    public void truncateData(){
        repositoryDataTemp.deleteAll();
    }

}
