package com.server.calc.service;

import com.server.calc.entity.DataFeatures;
import com.server.calc.repository.RepositoryDataFestures;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceDataFeatures {

    @Autowired
    RepositoryDataFestures repositoryDataFestures;

    public List<DataFeatures> getDataFeatures(String module, long menunumber, String keymenu){
        return null;
    }

}
