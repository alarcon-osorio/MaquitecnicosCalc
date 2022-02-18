package com.server.calc.controller;

import com.server.calc.dto.DataCalcDTO;
import com.server.calc.dto.DataCastResult;
import com.server.calc.dto.DataResult;
import com.server.calc.entity.*;
import com.server.calc.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Controller
@Slf4j
public class ControllerCalc {

    @Autowired
    ServiceDataStatic serviceDataStatic;

    @Autowired
    ServiceDataCalc serviceDataCalc;

    @Autowired
    ServiceDataProduct serviceDataProduct;

    @Autowired
    ServiceDataUsers serviceDataUsers;

    @Autowired
    ServiceDataTemp serviceDataTemp;

    @GetMapping("/calc")
    public String index(Model model, @AuthenticationPrincipal OidcUser principal){
        if (principal != null) {
            try{
                DataUsers dataUsers = serviceDataUsers.geyByEmail(principal.getEmail());
                if (dataUsers.getProfile().equals("admin")){
                    model.addAttribute("admin", dataUsers.getProfile());
                }
            }catch (NullPointerException ex){
                model.addAttribute("profile", principal.getClaims());
            }
            model.addAttribute("profile", principal.getClaims());
        }

        List<DataStatic> dataStaticList = serviceDataStatic.getAll();
        model.addAttribute("datastatic", dataStaticList);
        return "index";
    }

    @RequestMapping("/searchProducts")
    public String searchProduct(long importId, Model model, Integer pageNum) {

        if (importId == 4){
            if(pageNum==null){
                pageNum=1;
            }
            Pageable pageable = PageRequest.of(pageNum-1,15);
            Page<List<DataProduct>> productList = serviceDataProduct.getAllDataProductGeneral(pageable);
            model.addAttribute("productListSearch", productList);
            return "calculoExcel";
        }

        DataStatic dataStatic = serviceDataStatic.getById(importId);
        model.addAttribute("datastatic", dataStatic);

        DataCalc dataCalc = serviceDataCalc.getByConceptId(importId);
        model.addAttribute("datacalc", dataCalc);

        return "searchProducts";

    }

    @PostMapping("/printProducts")
    public String printProduct(DataProduct dataProduct, Model model) {

        String productData = dataProduct.getDescription();
        String[] prodData = productData.split(",");

        ArrayList<String> productRef = new ArrayList<>();
        ArrayList<String> productDesc = new ArrayList<>();
        ArrayList<String> productCant = new ArrayList<>();
        ArrayList<String> productImportId = new ArrayList<>();

        ArrayList<String> vipCalc = new ArrayList<>();
        ArrayList<String> distCalc = new ArrayList<>();
        ArrayList<String> conCalc = new ArrayList<>();
        ArrayList<String> pubCalc = new ArrayList<>();

        for (String data : prodData) {
            String[] splitData = data.split("- ");
            productRef.add(splitData[0]);
            productDesc.add(splitData[1]);
            productCant.add(splitData[2]);
            productImportId.add(splitData[3]);

            long quantity = Long.parseLong(splitData[2]);
            NumberFormat formatter = NumberFormat.getInstance(new Locale("en_US"));

            List<DataProduct> listDataProductValueDollar = serviceDataProduct.getListDataProductByValueDollar(splitData[0], quantity);
            log.info("listData" + listDataProductValueDollar);

            for (DataProduct dataProductValueDollar : listDataProductValueDollar) {

                DataCalcDTO dataCalcDTO = serviceDataCalc.getDataMasiva(dataProductValueDollar.getImportId());

                dataCalcDTO.setValueCop((long) (dataCalcDTO.getValueCop() * dataProductValueDollar.getValueDollar()));
                long legalization = dataCalcDTO.getLegalization();
                dataCalcDTO.setLegalization((dataCalcDTO.getValueCop() * legalization / 100) + dataCalcDTO.getValueCop());

                DataResult dataResult = new DataResult();

                float VIP = 0.65F;
                float DISTRIBUTOR = 0.6F;
                float CONSUMER = 0.5F;
                float PUBLICS = 0.4F;
                float calcLegalization = dataCalcDTO.getLegalization();

                dataResult.setVip((((int) Math.ceil(calcLegalization / VIP) + 99) / 100) * 100);
                dataResult.setDistributor((((int) Math.ceil(calcLegalization / DISTRIBUTOR) + 99) / 100) * 100);
                dataResult.setConsumer((((int) Math.ceil(calcLegalization / CONSUMER) + 99) / 100) * 100);
                dataResult.setPricePublic((((int) Math.ceil(calcLegalization / PUBLICS) + 99) / 100) * 100);

                BigDecimal bdVip = BigDecimal.valueOf(dataResult.getVip());
                BigDecimal bdDist = BigDecimal.valueOf(dataResult.getDistributor());
                BigDecimal bdCon = BigDecimal.valueOf(dataResult.getConsumer());
                BigDecimal bdPub = BigDecimal.valueOf(dataResult.getPricePublic());

                vipCalc.add(formatter.format(bdVip.longValue()));
                distCalc.add(formatter.format(bdDist.longValue()));
                conCalc.add(formatter.format(bdCon.longValue()));
                pubCalc.add(formatter.format(bdPub.longValue()));

                DataTemp dataTemp = new DataTemp();

                long id = 1;
                String imports = splitData[3];
                String ref =  splitData[0];
                String desc = splitData[1];
                String cant = splitData[2];

                String vip = bdVip.toString();
                String distri = bdDist.toString();
                String consu = bdCon.toString();
                String pub = bdPub.toString();
                String export = "1";

                /*
                dataTemp.setImports(splitData[3]);
                dataTemp.setRef(splitData[0]);
                dataTemp.setDesc(splitData[1]);
                dataTemp.setCant(splitData[2]);
                dataTemp.setVip(bdVip.toString());
                dataTemp.setDistri(bdDist.toString());
                dataTemp.setConsu(bdCon.toString());
                dataTemp.setPub(bdPub.toString());
                dataTemp.setExport("1");
                */

                serviceDataTemp.saveDataTemp(id, imports, ref, desc, cant, vip, distri, consu, pub, export);

                log.info("ImportId " + splitData[3]);
                log.info("productRef " + splitData[0]);
                log.info("productDesc " + splitData[1]);
                log.info("productCant " + splitData[2]);

                log.info(bdVip.toString());
                log.info(bdDist.toString());
                log.info(bdCon.toString());
                log.info(bdPub.toString());

            }

            model.addAttribute("productImportId", productImportId);
            model.addAttribute("productRef", productRef);
            model.addAttribute("productDesc", productDesc);
            model.addAttribute("productCant", productCant);

            model.addAttribute("vipCalc", vipCalc);
            model.addAttribute("distCalc", distCalc);
            model.addAttribute("conCalc", conCalc);
            model.addAttribute("pubCalc", pubCalc);



        }
        return "printProducts";

    }

    @GetMapping("/detailImport")
    public String detailImportNormalDT(String reference, long importId,  Model model) {

        if (importId == 1 || importId == 3) {
            List<DataProduct> dataProductList = serviceDataProduct.getDataProductReferenceList(reference, 1);
            if (dataProductList.isEmpty()) {
                DataStatic dataStatic = serviceDataStatic.getById(importId);
                model.addAttribute("datastatic", dataStatic);
                model.addAttribute("reference", reference);
                String err = "Error en campo por favor validar referencia";
                model.addAttribute("err", err);
                return "searchProducts";
            }
            model.addAttribute("dataproductlist", dataProductList);

        }else{
            List<DataProduct> dataProductList = serviceDataProduct.getDataProductReferenceList(reference, 2);
            if (dataProductList.isEmpty()) {
                DataStatic dataStatic = serviceDataStatic.getById(importId);
                model.addAttribute("datastatic", dataStatic);
                model.addAttribute("reference", reference);
                String err = "Error en campo por favor validar referencia";
                model.addAttribute("err", err);
                return "searchProducts";
            }
            model.addAttribute("dataproductlist", dataProductList);
        }

        DataStatic dataStatic = serviceDataStatic.getById(importId);
        model.addAttribute("datastatic", dataStatic);

        DataCalc dataCalc = serviceDataCalc.getByConceptId(importId);
        model.addAttribute("datacalc", dataCalc);

        return "detailImport";

    }

    @PostMapping("/resultCalc")
    public String resultCalc(@ModelAttribute("dataproduct") DataProduct dataProduct,  Model model) {

        try{
            DataCastResult dataCastResult = new DataCastResult();
            NumberFormat formatter = NumberFormat.getInstance(new Locale("en_US"));

            List<DataProduct> listDataProductValueDollar = serviceDataProduct.getListDataProductByValueDollar(dataProduct.getReference(), dataProduct.getAmount());

            for (DataProduct dataProductValueDollar : listDataProductValueDollar) {

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

            }

            return "resultCalc";

        }catch (Exception ex){
            model.addAttribute("error", ex);
            return "errorsTemplate";
        }

    }

}
