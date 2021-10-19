package com.server.calc.service;

import com.server.calc.entity.DataStatic;
import com.server.calc.repository.RepositoryDataStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceDataStatic {

    @Autowired
    RepositoryDataStatic repositoryDataStatic;

    public List<DataStatic> getAll(){
        return repositoryDataStatic.findAll();
    }

    public DataStatic getById(long importId){
        return repositoryDataStatic.findId(importId);
    }
}
