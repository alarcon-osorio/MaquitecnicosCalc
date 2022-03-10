package com.server.calc.controller;

import com.server.calc.entity.DataFeatures;
import com.server.calc.entity.DataProduct;
import com.server.calc.service.ServiceDataFeatures;
import com.server.calc.service.ServiceDataProduct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api")
@CrossOrigin(origins = "*")
@Log4j2
public class RestControllerApis {

    @Autowired
    ServiceDataProduct serviceDataProduct;

    @Autowired
    ServiceDataFeatures serviceDataFeatures;

    @GetMapping(value="/getProducts")
    public List<DataProduct> getProducts(){
        return serviceDataProduct.getAllDataProduct();
    }

    @GetMapping(value="/getProductsLimit")
    public List<DataProduct> getProductsLimit(int init, int end){
        return serviceDataProduct.getAllDataProductLimit(init, end);
    }

    @GetMapping(value="/getDataFeatures")
    public List<DataFeatures> getProductsLimit(String module, long menunumber, String keymenu ){
        return serviceDataFeatures.getDataFeatures(module, menunumber, keymenu);
    }

    @GetMapping(value="/getDataFeaturesMenuNumber")
    public Long getDataFeaturesMenuNumber(String menu ){
        return serviceDataFeatures.getDataFeaturesMenuNumber(menu);
    }

    @GetMapping(value="/getDataFeaturesModule")
    public String getDataFeaturesModule(String menu ){
        return serviceDataFeatures.getDataFeaturesModule(menu);
    }

}
