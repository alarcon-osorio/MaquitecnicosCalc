package com.server.calc.controller;

import com.server.calc.dto.DataCalcJoin;
import com.server.calc.entity.*;
import com.server.calc.exporter.ExcelExportUnlisted;
import com.server.calc.service.*;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@Log4j2
public class ControllerAdmin {

    @Autowired
    ServiceDataUsers serviceDataUsers;

    @Autowired
    ServiceDataCalc serviceDataCalc;

    @Autowired
    ServiceDataStatic serviceDataStatic;

    @Autowired
    ServiceDataTrm serviceDataTrm;

    @Autowired
    ServiceDataProduct serviceDataProduct;

    @Autowired
    ServiceDataRegistry serviceDataRegistry;

    @Autowired
    ServiceDataFeatures serviceDataFeatures;

    @GetMapping("/admin")
    public String admin(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal != null) {
            try{
                DataUsers dataUsers = serviceDataUsers.getByEmail(principal.getEmail());
                if (dataUsers.getProfile().equals("admin")){
                    model.addAttribute("admin", dataUsers.getProfile());
                    model.addAttribute("profile", principal.getClaims());
                    List<DataCalc> dataCalcList = serviceDataCalc.getAll();
                    if (!dataCalcList.isEmpty()){
                        for (DataCalc dataCalc: dataCalcList) {
                            model.addAttribute("trm", dataCalc.getValueCop());
                        }
                    }
                    return "admin";
                }else{
                    return "redirect:calc";
                }
            }catch (NullPointerException ex){
                return "redirect:calc";
            }
        }
        return "redirect:calc";
    }

    @GetMapping("/adminUnlisted")
    public String ulisted(Model model){
        List<DataRegistry> dataRegistryList = serviceDataRegistry.getDataRegistryAll();
        model.addAttribute("dataRegistryList", dataRegistryList);
        return "adminUnlisted";
    }

    @GetMapping("export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        log.info(response);
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=no_cotizados" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<DataRegistry> dataRegistries = serviceDataRegistry.getDataRegistryAll();

        ExcelExportUnlisted excelExporter = new ExcelExportUnlisted(dataRegistries);

        excelExporter.export(response);
    }

    @GetMapping("/adminTrm")
    public String trm(Model model){
        List<DataCalcJoin> trmList = serviceDataTrm.getDataTrmJoinList();
        log.info("Lista: " + trmList);
        model.addAttribute("trmList", trmList);
        return "adminTrm";
    }

    @GetMapping("/viewTrm")
    public String viewTrm(long id, Model model, boolean update){
        DataCalcJoin dataTrm = serviceDataTrm.getOneDataTrmJoin(id);
        if (update){
            model.addAttribute("edit", "Editado");
        }
        model.addAttribute("dataTrm", dataTrm);
        return "viewTrm";
    }

    @RequestMapping("/updateTrm")
    public String updateTrm(Model model, DataCalcJoin dataCalcJoin){
        log.info(dataCalcJoin);
        DataCalc dataCalc = new DataCalc();
        dataCalc.setId(dataCalcJoin.getId());
        dataCalc.setIdDataStatic(dataCalcJoin.getIdDatastatic());
        dataCalc.setValueCop(dataCalcJoin.getValueCop());
        dataCalc.setLegalization(dataCalcJoin.getLegalization());
        log.info(dataCalc);
        serviceDataCalc.updateDataCalc(dataCalc);
        return "redirect:viewTrm?id=" + dataCalc.getId() + "&update=true";
    }

    @RequestMapping("/deleteTrm")
    public String deleteTrm(long id, Model model){
        log.info(id);
        serviceDataCalc.deleteDatacalc(id);
        return "redirect:adminTrm";
    }

    @RequestMapping("/adminProducts")
    public String adminProducts(Model model, Integer pageNum){
        if(pageNum==null){
            pageNum=1;
        }
        Pageable pageable = PageRequest.of(pageNum-1,5);
        Page<List<DataProduct>> productList = serviceDataProduct.getAllDataProductGeneral(pageable);
        log.info("Lista de Productos: " + productList);
        model.addAttribute("productList", productList);
        return "adminProducts";
    }

    @GetMapping("/dataUsers")
    public String dataUsers(Model model){
        List<DataUsers> dataUsersList = serviceDataUsers.getAllUsers();
        log.info("Lista de Usuarios: " + dataUsersList);
        model.addAttribute("dataUsers", dataUsersList);
        return "adminUsers";
    }

    @GetMapping("/viewUsers")
    public String viewUsers(Model model, long id, boolean update){
        DataUsers dataUsers = serviceDataUsers.getDataUserById(id);
        log.info(dataUsers);
        if (update){
            model.addAttribute("edit", "Editado");
        }
        model.addAttribute("dataUsers", dataUsers);
        return "viewUsers";
    }

    @RequestMapping("/newUsers")
    public String newUsers(Model model, DataUsers dataUsers){
        return "viewUsers";
    }

    @RequestMapping("/updateUsers")
    public String viewUsers(Model model, DataUsers dataUsers){
        log.info(dataUsers);
        serviceDataUsers.updateUsers(dataUsers);
        return "redirect:viewUsers?id=" + dataUsers.getId() + "&update=true";
    }

    @RequestMapping("/deleteUsers")
    public String deleteUsers(Model model, long id){
        log.info(id);
        serviceDataUsers.deleteUsers(id);
        return "redirect:dataUsers";
    }

    @GetMapping("/adminFeatures")
    public String features(Model model){
        List<DataFeatures> dataFeaturesList = serviceDataFeatures.getAllDataFeatures();
        model.addAttribute("dataFeaturesList", dataFeaturesList);
        return "adminFeatures";
    }

    @GetMapping("/viewFeatures")
    public String viewFeatures(long id, Model model, boolean update){
        DataFeatures dataFeatures = serviceDataFeatures.getOneDataFeatures(id);
        if (update){
            model.addAttribute("edit", "Editado");
        }
        model.addAttribute("dataFeatures", dataFeatures);
        return "viewFeatures";
    }

    @RequestMapping("/editFeatures")
    public String editFeatures(Model model, DataFeatures dataFeatures){
        if (dataFeatures.getModule().equals("Calculadora") || dataFeatures.getModule().equals("calc")) {
            dataFeatures.setModule("calc");
        }else{
            dataFeatures.setModule("admin");
        }
        serviceDataFeatures.updateDatafeatures(dataFeatures);
        return "redirect:viewFeatures?id=" + dataFeatures.getId() + "&update=true";
    }

    @RequestMapping("/delete")
    public String deleteFeatures(Model model,
                                 Long idFeature, boolean deleteFeature,
                                 Long idUsers, boolean deleteUsers,
                                 Long idTrm, boolean deleteTrm,
                                 Long idProduct, boolean deleteProduct){

        if (idFeature != null){
            model.addAttribute("id", idFeature);
            model.addAttribute("type", "adminFeatures");
            model.addAttribute("delete", "deleteFeature");
        }

        if (deleteFeature){
            serviceDataFeatures.deleteDatafeatures(idFeature);
            return "redirect:adminFeatures";
        }

        if (idUsers != null){
            model.addAttribute("id", idUsers);
            model.addAttribute("type", "dataUsers");
            model.addAttribute("delete", "deleteUsers");
        }

        if (deleteUsers){
            serviceDataUsers.deleteUsers(idUsers);
            return "redirect:dataUsers";
        }

        if (idTrm != null){
            model.addAttribute("id", idTrm);
            model.addAttribute("type", "adminTrm");
            model.addAttribute("delete", "deleteTrm");
        }

        if (deleteTrm){
            serviceDataCalc.deleteDatacalc(idTrm);
            return "redirect:adminTrm";
        }

        if (idProduct != null){
            model.addAttribute("id", idProduct);
            model.addAttribute("type", "adminProducts");
            model.addAttribute("delete", "deleteProduct");
        }

        if (deleteProduct){
            //Delete Product - Service
            return "redirect:adminProducts";
        }

        return "confirmDelete";

    }

    @RequestMapping("/newFeatures")
    public String newFeatures(Model model, DataFeatures dataFeatures){
        List<String> dataFeaturesDistinctMenu = serviceDataFeatures.getDistinctMenu();
        List<String> dataFeaturesDistinctKeyMenu = serviceDataFeatures.getDistinctKeyMenu();
        List<DataFeatures> dataFeaturesList = serviceDataFeatures.getAllDataFeatures();
        model.addAttribute("dataFeaturesDistinctMenu", dataFeaturesDistinctMenu);
        model.addAttribute("dataFeaturesDistinctKeyMenu", dataFeaturesDistinctKeyMenu);
        model.addAttribute("dataFeaturesList", dataFeaturesList);
        return "viewFeatures";
    }

}
