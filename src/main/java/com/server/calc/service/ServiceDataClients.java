package com.server.calc.service;

import com.server.calc.entity.DataClients;
import com.server.calc.repository.RepositoryDataClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceDataClient {

  @Autowired
  RepositoryDataClient repositoryDataClient;

  public List<DataClients> getDataClients(){
    return repositoryDataClient.findAll();
  }

}
