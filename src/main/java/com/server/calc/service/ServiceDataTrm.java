package com.server.calc.service;

import com.server.calc.dto.DataCalcJoin;
import com.server.calc.repository.RepositoryDataTrm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceDataTrm {

    @Autowired
    RepositoryDataTrm repositoryDataTrm;

    public List<DataCalcJoin> getDataTrmJoinList(){
        return repositoryDataTrm.findDataCalcJoin();
    }

    public DataCalcJoin getOneDataTrmJoin(long id){
        return repositoryDataTrm.findDataCalcJoinById(id);
    }




}
