package com.server.calc.service;

import com.server.calc.entity.DataClients;
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

}
