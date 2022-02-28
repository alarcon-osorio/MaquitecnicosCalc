package com.server.calc.controller;

import com.server.calc.entity.DataProduct;
import com.server.calc.service.ServiceDataProduct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api")
@CrossOrigin(origins = "*")
@Log4j2
public class RestControllerApis {

    @Autowired
    ServiceDataProduct serviceDataProduct;

    @GetMapping(value="/getProducts")
    public List<DataProduct> getProducts(){
        return serviceDataProduct.getAllDataProduct();
    }

    @GetMapping(value="/getProductsLimit")
    public List<DataProduct> getProductsLimit(int init, int end){
        return serviceDataProduct.getAllDataProductLimit(init, end);
    }
}
