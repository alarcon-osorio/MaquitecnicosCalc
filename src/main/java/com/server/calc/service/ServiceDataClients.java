package com.server.calc.service;

import com.server.calc.entity.DataClients;
import com.server.calc.entity.DataFeatures;
import com.server.calc.repository.RepositoryDataClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceDataClients {

  @Autowired
  RepositoryDataClients repositoryDataClients;

  public List<DataClients> getDataClients(){
    return repositoryDataClients.findAll();
  }

  public DataClients getDataClientsByNit(String nit){
     return repositoryDataClients.findByNit(nit);
  }

  public void saveDataClients(DataClients dataClients){
    repositoryDataClients.save(dataClients);
  }

  public void updateDataClients(DataClients dataClients){
    repositoryDataClients.save(dataClients);
  }

  public DataClients getOneDataClients(long id){
    return repositoryDataClients.findById(id).get();
  }

}
