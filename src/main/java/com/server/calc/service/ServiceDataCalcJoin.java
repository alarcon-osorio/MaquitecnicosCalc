package com.server.calc.service;

import com.server.calc.dto.DataCalcJoin;
import com.server.calc.repository.RepositoryDataCalcJoin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceDataCalcJoin {

    @Autowired
    RepositoryDataCalcJoin repositoryDataCalcJoin;

    public List<DataCalcJoin> getDataCalcJoinList(){
        return repositoryDataCalcJoin.findDataCalcJoin();
    }


}
