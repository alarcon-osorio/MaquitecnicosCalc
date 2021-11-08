package com.server.calc.controller;

import com.server.calc.dto.DataCastResult;
import com.server.calc.dto.DataResult;
import com.server.calc.entity.DataCalc;
import com.server.calc.entity.DataProduct;
import com.server.calc.entity.DataStatic;
import com.server.calc.service.ServiceDataCalc;
import com.server.calc.service.ServiceDataProduct;
import com.server.calc.service.ServiceDataStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


@Controller
public class ControllerCalc {

    @Autowired
    ServiceDataStatic serviceDataStatic;

    @Autowired
    ServiceDataCalc serviceDataCalc;

    @Autowired
    ServiceDataProduct serviceDataProduct;

    @GetMapping("/calc")
    public String index(Model model){

        List<DataStatic> dataStaticList = serviceDataStatic.getAll();
        model.addAttribute("datastatic", dataStaticList);
        return "index";
    }

    @GetMapping("/searchProducts")
    public String searchProduct(long importId, Model model) {

        DataStatic dataStatic = serviceDataStatic.getById(importId);
        model.addAttribute("datastatic", dataStatic);

        DataCalc dataCalc = serviceDataCalc.getByConceptId(importId);
        model.addAttribute("datacalc", dataCalc);

        return "searchProducts";
    }

    @GetMapping("/detailImport")
    public String detailImportNormalDT(String reference, long importId,  Model model) {


        DataProduct dataProduct = serviceDataProduct.getDataProductReference(reference);
        model.addAttribute("dataproduct", dataProduct);


        DataStatic dataStatic = serviceDataStatic.getById(importId);
        model.addAttribute("datastatic", dataStatic);

        /*
        DataCalc dataCalc = serviceDataCalc.getByConceptId(importId);
        model.addAttribute("datacalc", dataCalc);
        */

        return "detailImport";
    }

    @PostMapping("/resultCalc")
    public String resultCalc(@ModelAttribute("dataproduct") DataProduct dataProduct, Model model) {

        DataProduct dataProductData = serviceDataProduct.getDataProduct(dataProduct.getId());
        dataProductData.setImportId(dataProduct.getImportId());
        dataProductData.setReference(dataProductData.getReference());
        dataProductData.setDescription(dataProductData.getDescription());
        dataProductData.setValueDollar(dataProduct.getValueDollar());
        serviceDataProduct.updateDataProduct(dataProductData);
        model.addAttribute("dataproductdata", dataProductData);

        DataCalc dataCalc = serviceDataCalc.getByConceptId(dataProduct.getImportId());
        dataCalc.setValueCop(dataCalc.getValueCop() * dataProduct.getValueDollar());
        long legalization = dataCalc.getLegalization();
        dataCalc.setLegalization((dataCalc.getValueCop() * legalization / 100) + dataCalc.getValueCop());
        System.out.println(dataCalc);
        model.addAttribute("datacalc", dataCalc);

        DataResult dataResult = new DataResult();
        float VIP = 0.65F;
        float DISTRIBUTOR = 0.6F;
        float CONSUMER = 0.5F;
        float PUBLICS = 0.4F;
        float calcLegalization = dataCalc.getLegalization();
        dataResult.setVip(Math.round(calcLegalization / VIP));
        dataResult.setDistributor(Math.round(calcLegalization / DISTRIBUTOR));
        dataResult.setConsumer(Math.round(calcLegalization / CONSUMER));
        dataResult.setPricePublic(Math.round(calcLegalization / PUBLICS));

        DataCastResult dataCastResult = new DataCastResult();
        BigDecimal bdVip = new BigDecimal(dataResult.getVip());
        BigDecimal bdDist = new BigDecimal(dataResult.getDistributor());
        BigDecimal bdCon = new BigDecimal(dataResult.getConsumer());
        BigDecimal bdPub = new BigDecimal(dataResult.getPricePublic());
        NumberFormat formatter = NumberFormat.getInstance(new Locale("en_US"));
        dataCastResult.setVipCast(formatter.format(bdVip.longValue()));
        dataCastResult.setDistributorCast(formatter.format(bdDist.longValue()));
        dataCastResult.setConsumerCast(formatter.format(bdCon.longValue()));
        dataCastResult.setPricePublicCast(formatter.format(bdPub.longValue()));
        model.addAttribute("datacastresult", dataCastResult);

        return "resultCalc";
    }

}
