package com.server.calc.service;

import com.server.calc.entity.DataRegistry;
import com.server.calc.repository.RepositoryDataRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceDataRegistry {

    @Autowired
    RepositoryDataRegistry repositoryDataRegistry;

    public void saveDataRegistry(DataRegistry dataRegistry){
        repositoryDataRegistry.save(dataRegistry);
    }

    public List<DataRegistry> getDataRegistryAll(){
        return repositoryDataRegistry.findAll();
    }

}
