package com.server.calc.service;

import com.server.calc.entity.DataFeatures;
import com.server.calc.repository.RepositoryDataFeatures;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceDataFeatures {

    @Autowired
    RepositoryDataFeatures repositoryDataFeatures;

    public List<DataFeatures> getDataFeatures(String module, long menunumber, String keymenu){
        return repositoryDataFeatures.findDataFeatures(module, menunumber, keymenu);
    }

    public List<DataFeatures> getAllDataFeatures(){
        return repositoryDataFeatures.findAll();
    }

    public DataFeatures getOneDataFeatures(long id){
        return repositoryDataFeatures.findById(id).get();
    }

    public void updateDatafeatures(DataFeatures dataFeatures){
        repositoryDataFeatures.save(dataFeatures);
    }

    public List<String> getDistinctMenu(){
        return repositoryDataFeatures.findDistincMenu();
    }

    public List<String> getDistinctKeyMenu(){
        return repositoryDataFeatures.findDistinctKeyMenu();
    }

    public Long getDataFeaturesMenuNumber(String menu){
        return repositoryDataFeatures.findDataFeaturesMenuNumber(menu);
    }

    public String getDataFeaturesModule(String menu){
        return repositoryDataFeatures.findDataFeaturesModule(menu);
    }

    public void deleteDatafeatures(Long id){
        repositoryDataFeatures.deleteById(id);
    }

}
