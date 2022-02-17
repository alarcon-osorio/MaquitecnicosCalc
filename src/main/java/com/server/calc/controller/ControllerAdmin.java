package com.server.calc.controller;

import com.server.calc.dto.DataCalcJoin;
import com.server.calc.entity.DataCalc;
import com.server.calc.entity.DataProduct;
import com.server.calc.entity.DataUsers;
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
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
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

    @GetMapping("/trm")
    public String trm(Model model){
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

}
