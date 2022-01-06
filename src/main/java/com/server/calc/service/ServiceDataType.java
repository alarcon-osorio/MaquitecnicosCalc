package com.server.calc.service;

import com.server.calc.entity.DataType;
import com.server.calc.repository.RepositoryDataType;
import org.springframework.beans.factory.annotation.Autowired;

public class ServiceDataType {

    @Autowired
    RepositoryDataType repositoryDataType;

    public DataType getById(long importId){
        return repositoryDataType.findId(importId);
    }
}
