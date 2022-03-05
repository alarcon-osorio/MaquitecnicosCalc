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
        return repositoryDataFestures.findDataFeatures(module, menunumber, keymenu);
    }

    public List<DataFeatures> getAllDataFeatures(){
        return repositoryDataFestures.findAll();
    }

    public DataFeatures getOneDataFeatures(long id){
        return repositoryDataFestures.findById(id).get();
    }

    public void updateDatafeatures(DataFeatures dataFeatures){
        repositoryDataFestures.save(dataFeatures);
    }

    public List<String> getDistinctMenu(){
        return repositoryDataFestures.findDistincMenu();
    }

    public List<String> getDistinctKeyMenu(){
        return repositoryDataFestures.findDistinctKeyMenu();
    }

    public Long getDataFeaturesMenuNumber(String menu){
        return repositoryDataFestures.findDataFeaturesMenuNumber(menu);
    }

    public String getDataFeaturesModule(String menu){
        return repositoryDataFestures.findDataFeaturesModule(menu);
    }

    public void deleteDatafeatures(Long id){
        repositoryDataFestures.deleteById(id);
    }

}
