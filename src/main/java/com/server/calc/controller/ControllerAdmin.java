package com.server.calc.controller;

import com.server.calc.dto.DataCalcJoin;
import com.server.calc.entity.DataCalc;
import com.server.calc.entity.DataProduct;
import com.server.calc.entity.DataRegistry;
import com.server.calc.entity.DataUsers;
import com.server.calc.exporter.ExcelExportUnlisted;
import com.server.calc.service.*;
import lombok.extern.log4j.Log4j2;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

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
    ServiceDataCalcJoin serviceDataCalcJoin;

    @Autowired
    ServiceDataProduct serviceDataProduct;

    @Autowired
    ServiceDataRegistry serviceDataRegistry;

    @GetMapping("/admin")
    public String admin(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal != null) {
            try{
                DataUsers dataUsers = serviceDataUsers.geyByEmail(principal.getEmail());
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

    @GetMapping("/adminTrm")
    public String trm(Model model){
        List<DataCalcJoin> trmList = serviceDataCalcJoin.getDataCalcJoinList();
        log.info("Lista: " + trmList);
        model.addAttribute("trmList", trmList);
        return "adminTrm";
    }

    @PutMapping("/updateTrm")
    public String updateTrm(Model model){
        List<DataCalcJoin> trmList = serviceDataCalcJoin.getDataCalcJoinList();
        log.info("Lista: " + trmList);
        model.addAttribute("trmList", trmList);
        return "adminTrm";
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

}
