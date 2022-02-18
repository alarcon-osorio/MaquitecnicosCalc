package com.server.calc.service;

import com.server.calc.entity.DataTemp;
import com.server.calc.repository.RepositoryDataTemp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceDataTemp {

    @Autowired
    RepositoryDataTemp repositoryDataTemp;

    public DataTemp saveDataTemp(DataTemp dataTemp){
        return repositoryDataTemp.save(dataTemp);
    }

}
