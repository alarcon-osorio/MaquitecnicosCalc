package com.server.calc.service;

import com.server.calc.entity.DataUsers;
import com.server.calc.repository.RepositoryDataUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceDataUsers {

    @Autowired
    RepositoryDataUsers repositoryDataUsers;

    public List<DataUsers> getAllUsers(){
        return repositoryDataUsers.findAllUsers();
    }

    public DataUsers geyByEmail(String email){
        return repositoryDataUsers.findByEmail(email);
    }



}
