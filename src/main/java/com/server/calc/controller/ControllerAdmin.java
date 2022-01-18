package com.server.calc.controller;

import com.server.calc.dto.DataCalcJoin;
import com.server.calc.entity.DataCalc;
import com.server.calc.entity.DataStatic;
import com.server.calc.entity.DataUsers;
import com.server.calc.service.ServiceDataCalc;
import com.server.calc.service.ServiceDataStatic;
import com.server.calc.service.ServiceDataUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedList;
import java.util.List;

@Controller
public class ControllerAdmin {

    @Autowired
    ServiceDataUsers serviceDataUsers;

    @Autowired
    ServiceDataCalc serviceDataCalc;

    @Autowired
    ServiceDataStatic serviceDataStatic;

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
        List<DataCalc> trmList = serviceDataCalc.getAll();
        List<DataStatic> typeList = serviceDataStatic.getAll();
        model.addAttribute("trmList", trmList);
        return "trm";
    }

}
