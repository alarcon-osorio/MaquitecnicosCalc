package com.server.calc.service;

import com.server.calc.entity.DataUsers;
import com.server.calc.repository.RepositoryDataUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceDataUsers {

    @Autowired
    RepositoryDataUsers repositoryDataUsers;

    public DataUsers geyByEmail(String email){
        return repositoryDataUsers.findByEmail(email);
    }



}
