package com.server.calc.service;

import com.server.calc.entity.DataCalc;
import com.server.calc.repository.RepositoryDataCalc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceDataCalc {

    @Autowired
    RepositoryDataCalc repositoryDataCalc;

    public List<DataCalc> getAll(){
        return repositoryDataCalc.findAll();
    }

    public DataCalc getByConceptId(long idDataStatic){
        return repositoryDataCalc.findBydDataStaticId(idDataStatic);
    }


}
