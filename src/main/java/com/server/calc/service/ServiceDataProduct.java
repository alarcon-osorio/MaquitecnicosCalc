package com.server.calc.service;

import com.server.calc.entity.DataProduct;
import com.server.calc.repository.RepositoryDataProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceDataProduct {

    @Autowired
    RepositoryDataProduct repositoryDataProduct;

    public Page<List<DataProduct>> getAllDataProductGeneral(Pageable pageable) {
        return repositoryDataProduct.findAllDataProductGeneral(pageable);
    }

    public List<DataProduct> getAllDataProduct() {
        return repositoryDataProduct.findAllDataProduct();
    }

    public List<DataProduct> getAllDataProductLimit(int init, int end) {
        return repositoryDataProduct.findAllDataProductLimit(init, end);
    }

    public DataProduct getDataProduct(Long productid){
        return repositoryDataProduct.findId(productid);
    }

    public List<DataProduct> getDataProductReferenceList(String reference, long importId){
        return repositoryDataProduct.findByReferenceList(reference, importId);
    }

    public DataProduct getDataProductByReference(String reference){
        return repositoryDataProduct.findByReference(reference);
    }

    public List<DataProduct> getListDataProductByValueDollar(String reference, long amount){
        return repositoryDataProduct.findListByValueDollar(reference, amount);
    }

    public void updateDataProduct(DataProduct dataProduct){
        repositoryDataProduct.save(dataProduct);
    }


}
