package com.server.calc.service;

import com.server.calc.entity.DataProduct;
import com.server.calc.repository.RepositoryDataProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceDataProduct {

    @Autowired
    RepositoryDataProduct repositoryDataProduct;

    public List<DataProduct> getAllDataProduct() {
        return repositoryDataProduct.findAll();
    }

    public DataProduct getDataProduct(Long productid){
        return repositoryDataProduct.findId(productid);
    }

    public DataProduct getDataProductReference(String reference){
        return repositoryDataProduct.findByReference(reference);
    }

    public void updateDataProduct(DataProduct dataProduct){
        repositoryDataProduct.save(dataProduct);
    }


}
