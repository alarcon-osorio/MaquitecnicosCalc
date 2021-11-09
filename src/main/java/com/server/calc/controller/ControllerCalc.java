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

        List<DataProduct> dataProductList = serviceDataProduct.getDataProductReferenceList(reference);
        System.out.println("dataProductList" + dataProductList);

        //TODO: EN EL REPOSITORIO SE PUEDE HACER LA CONSULTA

        if (dataProductList.size() == 0) {
            DataStatic dataStatic = serviceDataStatic.getById(importId);
            model.addAttribute("datastatic", dataStatic);
            model.addAttribute("reference", reference);
            String err = "Error en campo por favor validar referencia";
            model.addAttribute("err", err);
            return "searchProducts";
        }
        model.addAttribute("dataproductlist", dataProductList);

        DataStatic dataStatic = serviceDataStatic.getById(importId);
        model.addAttribute("datastatic", dataStatic);

        DataCalc dataCalc = serviceDataCalc.getByConceptId(importId);
        model.addAttribute("datacalc", dataCalc);

        return "detailImport";

    }

    @PostMapping("/resultCalc")
    public String resultCalc(@ModelAttribute("dataproduct") DataProduct dataProduct, Model model) {

        try{
            DataCastResult dataCastResult = new DataCastResult();
            NumberFormat formatter = NumberFormat.getInstance(new Locale("en_US"));

            DataProduct dataProductValueDollar = serviceDataProduct.getDataProductByValueDollar(dataProduct.getReference(), dataProduct.getAmount());

            DataProduct dataProductData = serviceDataProduct.getDataProduct(dataProductValueDollar.getId());
            model.addAttribute("dataproductdata", dataProductData);

            DataStatic dataStatic = serviceDataStatic.getById(dataProduct.getImportId());
            model.addAttribute("datastatic", dataStatic);

            DataCalc dataCalc = serviceDataCalc.getByConceptId(dataProduct.getImportId());
            dataCalc.setValueCop((long) (dataCalc.getValueCop() * dataProductValueDollar.getValueDollar()));
            long legalization = dataCalc.getLegalization();
            dataCalc.setLegalization((dataCalc.getValueCop() * legalization / 100) + dataCalc.getValueCop());

            BigDecimal bdValueCop = new BigDecimal(dataCalc.getValueCop());
            BigDecimal bdLegalization = new BigDecimal(dataCalc.getLegalization());

            dataCastResult.setValueCopCast(formatter.format(bdValueCop.longValue()));
            dataCastResult.setLegalizationCast(formatter.format(bdLegalization.longValue()));

            model.addAttribute("datacalc", dataCastResult);

            DataResult dataResult = new DataResult();
            float VIP = 0.65F;
            float DISTRIBUTOR = 0.6F;
            float CONSUMER = 0.5F;
            float PUBLICS = 0.4F;
            float calcLegalization = dataCalc.getLegalization();

            dataResult.setVip((((int) Math.ceil(calcLegalization / VIP) + 99) / 100 ) * 100);
            dataResult.setDistributor((((int) Math.ceil(calcLegalization / DISTRIBUTOR) + 99) / 100 ) * 100);
            dataResult.setConsumer((((int) Math.ceil(calcLegalization / CONSUMER) + 99) / 100) * 100 );
            dataResult.setPricePublic((((int) Math.ceil(calcLegalization / PUBLICS) + 99) / 100) * 100 );

            BigDecimal bdVip = new BigDecimal(dataResult.getVip());
            BigDecimal bdDist = new BigDecimal(dataResult.getDistributor());
            BigDecimal bdCon = new BigDecimal(dataResult.getConsumer());
            BigDecimal bdPub = new BigDecimal(dataResult.getPricePublic());

            dataCastResult.setVipCast(formatter.format(bdVip.longValue()));
            dataCastResult.setDistributorCast(formatter.format(bdDist.longValue()));
            dataCastResult.setConsumerCast(formatter.format(bdCon.longValue()));
            dataCastResult.setPricePublicCast(formatter.format(bdPub.longValue()));

            model.addAttribute("datacastresult", dataCastResult);

            return "resultCalc";

        }catch (Exception ex){
            model.addAttribute("error", ex);
            return "errorsTemplate";
        }

    }

}
