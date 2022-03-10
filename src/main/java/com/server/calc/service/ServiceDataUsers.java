package com.server.calc.service;

import com.server.calc.entity.DataUsers;
import com.server.calc.repository.RepositoryDataUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceDataUsers {

    @Autowired
    RepositoryDataUsers repositoryDataUsers;

    public List<DataUsers> getAllUsers(){
        return repositoryDataUsers.findAll();
    }

    public DataUsers getByEmail(String email){
        return repositoryDataUsers.findByEmail(email);
    }

    public DataUsers getDataUserById(long id){
        return repositoryDataUsers.findById(id).get();
    }

    public void updateUsers(DataUsers dataUsers){
        repositoryDataUsers.save(dataUsers);
    }

    public void addUsers(DataUsers dataUsers){
        repositoryDataUsers.save(dataUsers);
    }

    public void deleteUsers(long id){
        repositoryDataUsers.deleteById(id);
    }

}
