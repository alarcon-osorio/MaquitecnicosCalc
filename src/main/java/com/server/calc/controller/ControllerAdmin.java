package com.server.calc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.calc.dto.*;
import com.server.calc.entity.*;
import com.server.calc.exporter.ExcelExportUnlisted;
import com.server.calc.repository.RepositoryDataUsers;
import com.server.calc.service.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    RepositoryDataUsers repositoryDataUsers;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ServiceDataClients serviceDataClients;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @GetMapping("/admin")
    public String admin(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal != null) {
            try {
                DataUsers dataUsers = serviceDataUsers.getByEmail(principal.getEmail());
                if (dataUsers.getProfile().equals("admin")) {
                    model.addAttribute("admin", dataUsers.getProfile());
                    model.addAttribute("profile", principal.getClaims());
                    List<DataCalc> dataCalcList = serviceDataCalc.getAll();
                    if (!dataCalcList.isEmpty()) {
                        for (DataCalc dataCalc : dataCalcList) {
                            model.addAttribute("trm", dataCalc.getValueCop());
                        }
                    }
                    return "admin";
                } else {
                    return "redirect:calc";
                }
            } catch (NullPointerException ex) {
                return "redirect:calc";
            }
        }
        return "redirect:calc";
    }

    @GetMapping("/adminUnlisted")
    public String ulisted(Model model) {
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
    public String trm(Model model) {
        List<DataCalcJoin> trmList = serviceDataTrm.getDataTrmJoinList();
        log.info("Lista: " + trmList);
        model.addAttribute("trmList", trmList);
        return "adminTrm";
    }

    @GetMapping("/viewTrm")
    public String viewTrm(long id, Model model, boolean update) {
        DataCalcJoin dataTrm = serviceDataTrm.getOneDataTrmJoin(id);
        if (update) {
            model.addAttribute("edit", "Editado");
        }
        model.addAttribute("dataTrm", dataTrm);
        return "viewTrm";
    }

    @RequestMapping("/updateTrm")
    public String updateTrm(Model model, DataCalcJoin dataCalcJoin) {
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
    public String deleteTrm(long id, Model model) {
        log.info(id);
        serviceDataCalc.deleteDatacalc(id);
        return "redirect:adminTrm";
    }

    @RequestMapping("/adminProducts")
    public String adminProducts(Model model, Integer pageNum) {
        if (pageNum == null) {
            pageNum = 1;
        }
        Pageable pageable = PageRequest.of(pageNum - 1, 5);
        Page<List<DataProduct>> productList = serviceDataProduct.getAllDataProductGeneral(pageable);
        log.info("Lista de Productos: " + productList);
        model.addAttribute("productList", productList);
        return "adminProducts";
    }

    @RequestMapping(value = "/dataUsers")
    public String getUsersAuth(Model model) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String token = getToken().getAccess_token();

        headers.add("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> response = (new RestTemplate()).exchange("https://auth-projects.us.auth0.com/api/v2/users", HttpMethod.GET, entity, String.class);

        UsersDTO[] usersDTO = mapper.readValue(response.getBody(), UsersDTO[].class);

        for (UsersDTO usersAdd : usersDTO) {
            DataUsers userExist = serviceDataUsers.getByEmail(usersAdd.getEmail());
            if (userExist == null) {
                String name = usersAdd.getName();
                String email = usersAdd.getEmail();
                String login = usersAdd.getNickname();
                String password = "***********";
                String profile = "user";
                String state = "Activo";
                String authid = usersAdd.getUser_id();
                serviceDataUsers.addUsersIdempotente(name, email, login, password, profile, state, authid);
            }
        }

        List<DataUsers> usersList = serviceDataUsers.getAllUsers();

        model.addAttribute("usuarios", usersList);
        return "adminUsers";
    }


    public DataTokenResponse getToken (){
        DataToken dataToken = new DataToken();
        dataToken.setClient_id("16gHvUIkwjJH6DdxdX6eIqV9BTIShNGu");
        dataToken.setGrant_type("client_credentials");
        dataToken.setClient_secret("puSlQ2A-rM2ad7DY6OHgFgpeS7t5Ofyueb8oambYTTq4L_HcBhjvGSw12Sjia3cT");
        dataToken.setAudience("https://auth-projects.us.auth0.com/api/v2/");

        String URI_TOKEN = "https://auth-projects.us.auth0.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<DataToken> entity = new HttpEntity<DataToken>(dataToken,headers);

        ResponseEntity<DataTokenResponse> response = restTemplate.exchange(
                URI_TOKEN, HttpMethod.POST, entity, DataTokenResponse.class);

        log.info(response.getBody());

        return response.getBody();

    }

    @GetMapping("/viewUsers")
    public String viewUsers(Model model, long id, boolean update, boolean duplicate, boolean password) {
        DataUsers dataUsers = serviceDataUsers.getDataUserById(id);

        if (update) {
            model.addAttribute("edit", "Editado");
        }

        if (duplicate) {
            model.addAttribute("conflict", "Err Email");
        }

        if (password) {
            model.addAttribute("password", "Err password");
        }

        model.addAttribute("dataUsers", dataUsers);
        return "viewUsers";
    }

    @RequestMapping("/newUsers")
    public String newUsers(Model model, DataUsers dataUsers, boolean password, boolean conflict) {
        if (password) {
            model.addAttribute("password", "Err password");
        }

        if (conflict) {
            model.addAttribute("conflict", "Err Email");
        }

        return "viewUsers";
    }

    @RequestMapping("/saveUsers")
    public String saveUsers(Model model, DataUsers dataUsers, String type) {

        if (Objects.equals(type, "add")) {
            try {
                //Crea registro en AUTH0
                Date date = new Date();
                SimpleDateFormat formateadorFecha = new SimpleDateFormat("ddMMyyyy");

                NewUsersDTO newUsersDTO = new NewUsersDTO();
                newUsersDTO.setName(dataUsers.getName());
                newUsersDTO.setEmail(dataUsers.getEmail());
                newUsersDTO.setNickname(dataUsers.getLogin());
                newUsersDTO.setUsername(dataUsers.getLogin());
                newUsersDTO.setPassword(dataUsers.getPassword());
                newUsersDTO.setVerify_email(true);
                newUsersDTO.setUser_id("2022fff" + formateadorFecha.format(date) + "dbmaq");
                newUsersDTO.setConnection("Username-Password-Authentication");

                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InJxSEU5alBHOXkzU1Z6SDFHU2t4bCJ9.eyJpc3MiOiJodHRwczovL2F1dGgtcHJvamVjdHMudXMuYXV0aDAuY29tLyIsInN1YiI6IkE4N0tlQUVBdVpwQ3hCN0d6clhHSDJKS3UxQUFINVAwQGNsaWVudHMiLCJhdWQiOiJodHRwczovL2F1dGgtcHJvamVjdHMudXMuYXV0aDAuY29tL2FwaS92Mi8iLCJpYXQiOjE2NDgwMDY1MzAsImV4cCI6MTY1MDU5ODUzMCwiYXpwIjoiQTg3S2VBRUF1WnBDeEI3R3pyWEdIMkpLdTFBQUg1UDAiLCJzY29wZSI6InJlYWQ6Y2xpZW50X2dyYW50cyBjcmVhdGU6Y2xpZW50X2dyYW50cyBkZWxldGU6Y2xpZW50X2dyYW50cyB1cGRhdGU6Y2xpZW50X2dyYW50cyByZWFkOnVzZXJzIHVwZGF0ZTp1c2VycyBkZWxldGU6dXNlcnMgY3JlYXRlOnVzZXJzIHJlYWQ6dXNlcnNfYXBwX21ldGFkYXRhIHVwZGF0ZTp1c2Vyc19hcHBfbWV0YWRhdGEgZGVsZXRlOnVzZXJzX2FwcF9tZXRhZGF0YSBjcmVhdGU6dXNlcnNfYXBwX21ldGFkYXRhIHJlYWQ6dXNlcl9jdXN0b21fYmxvY2tzIGNyZWF0ZTp1c2VyX2N1c3RvbV9ibG9ja3MgZGVsZXRlOnVzZXJfY3VzdG9tX2Jsb2NrcyBjcmVhdGU6dXNlcl90aWNrZXRzIHJlYWQ6Y2xpZW50cyB1cGRhdGU6Y2xpZW50cyBkZWxldGU6Y2xpZW50cyBjcmVhdGU6Y2xpZW50cyByZWFkOmNsaWVudF9rZXlzIHVwZGF0ZTpjbGllbnRfa2V5cyBkZWxldGU6Y2xpZW50X2tleXMgY3JlYXRlOmNsaWVudF9rZXlzIHJlYWQ6Y29ubmVjdGlvbnMgdXBkYXRlOmNvbm5lY3Rpb25zIGRlbGV0ZTpjb25uZWN0aW9ucyBjcmVhdGU6Y29ubmVjdGlvbnMgcmVhZDpyZXNvdXJjZV9zZXJ2ZXJzIHVwZGF0ZTpyZXNvdXJjZV9zZXJ2ZXJzIGRlbGV0ZTpyZXNvdXJjZV9zZXJ2ZXJzIGNyZWF0ZTpyZXNvdXJjZV9zZXJ2ZXJzIHJlYWQ6ZGV2aWNlX2NyZWRlbnRpYWxzIHVwZGF0ZTpkZXZpY2VfY3JlZGVudGlhbHMgZGVsZXRlOmRldmljZV9jcmVkZW50aWFscyBjcmVhdGU6ZGV2aWNlX2NyZWRlbnRpYWxzIHJlYWQ6cnVsZXMgdXBkYXRlOnJ1bGVzIGRlbGV0ZTpydWxlcyBjcmVhdGU6cnVsZXMgcmVhZDpydWxlc19jb25maWdzIHVwZGF0ZTpydWxlc19jb25maWdzIGRlbGV0ZTpydWxlc19jb25maWdzIHJlYWQ6aG9va3MgdXBkYXRlOmhvb2tzIGRlbGV0ZTpob29rcyBjcmVhdGU6aG9va3MgcmVhZDphY3Rpb25zIHVwZGF0ZTphY3Rpb25zIGRlbGV0ZTphY3Rpb25zIGNyZWF0ZTphY3Rpb25zIHJlYWQ6ZW1haWxfcHJvdmlkZXIgdXBkYXRlOmVtYWlsX3Byb3ZpZGVyIGRlbGV0ZTplbWFpbF9wcm92aWRlciBjcmVhdGU6ZW1haWxfcHJvdmlkZXIgYmxhY2tsaXN0OnRva2VucyByZWFkOnN0YXRzIHJlYWQ6aW5zaWdodHMgcmVhZDp0ZW5hbnRfc2V0dGluZ3MgdXBkYXRlOnRlbmFudF9zZXR0aW5ncyByZWFkOmxvZ3MgcmVhZDpsb2dzX3VzZXJzIHJlYWQ6c2hpZWxkcyBjcmVhdGU6c2hpZWxkcyB1cGRhdGU6c2hpZWxkcyBkZWxldGU6c2hpZWxkcyByZWFkOmFub21hbHlfYmxvY2tzIGRlbGV0ZTphbm9tYWx5X2Jsb2NrcyB1cGRhdGU6dHJpZ2dlcnMgcmVhZDp0cmlnZ2VycyByZWFkOmdyYW50cyBkZWxldGU6Z3JhbnRzIHJlYWQ6Z3VhcmRpYW5fZmFjdG9ycyB1cGRhdGU6Z3VhcmRpYW5fZmFjdG9ycyByZWFkOmd1YXJkaWFuX2Vucm9sbG1lbnRzIGRlbGV0ZTpndWFyZGlhbl9lbnJvbGxtZW50cyBjcmVhdGU6Z3VhcmRpYW5fZW5yb2xsbWVudF90aWNrZXRzIHJlYWQ6dXNlcl9pZHBfdG9rZW5zIGNyZWF0ZTpwYXNzd29yZHNfY2hlY2tpbmdfam9iIGRlbGV0ZTpwYXNzd29yZHNfY2hlY2tpbmdfam9iIHJlYWQ6Y3VzdG9tX2RvbWFpbnMgZGVsZXRlOmN1c3RvbV9kb21haW5zIGNyZWF0ZTpjdXN0b21fZG9tYWlucyB1cGRhdGU6Y3VzdG9tX2RvbWFpbnMgcmVhZDplbWFpbF90ZW1wbGF0ZXMgY3JlYXRlOmVtYWlsX3RlbXBsYXRlcyB1cGRhdGU6ZW1haWxfdGVtcGxhdGVzIHJlYWQ6bWZhX3BvbGljaWVzIHVwZGF0ZTptZmFfcG9saWNpZXMgcmVhZDpyb2xlcyBjcmVhdGU6cm9sZXMgZGVsZXRlOnJvbGVzIHVwZGF0ZTpyb2xlcyByZWFkOnByb21wdHMgdXBkYXRlOnByb21wdHMgcmVhZDpicmFuZGluZyB1cGRhdGU6YnJhbmRpbmcgZGVsZXRlOmJyYW5kaW5nIHJlYWQ6bG9nX3N0cmVhbXMgY3JlYXRlOmxvZ19zdHJlYW1zIGRlbGV0ZTpsb2dfc3RyZWFtcyB1cGRhdGU6bG9nX3N0cmVhbXMgY3JlYXRlOnNpZ25pbmdfa2V5cyByZWFkOnNpZ25pbmdfa2V5cyB1cGRhdGU6c2lnbmluZ19rZXlzIHJlYWQ6bGltaXRzIHVwZGF0ZTpsaW1pdHMgY3JlYXRlOnJvbGVfbWVtYmVycyByZWFkOnJvbGVfbWVtYmVycyBkZWxldGU6cm9sZV9tZW1iZXJzIHJlYWQ6ZW50aXRsZW1lbnRzIHJlYWQ6YXR0YWNrX3Byb3RlY3Rpb24gdXBkYXRlOmF0dGFja19wcm90ZWN0aW9uIHJlYWQ6b3JnYW5pemF0aW9uc19zdW1tYXJ5IHJlYWQ6b3JnYW5pemF0aW9ucyB1cGRhdGU6b3JnYW5pemF0aW9ucyBjcmVhdGU6b3JnYW5pemF0aW9ucyBkZWxldGU6b3JnYW5pemF0aW9ucyBjcmVhdGU6b3JnYW5pemF0aW9uX21lbWJlcnMgcmVhZDpvcmdhbml6YXRpb25fbWVtYmVycyBkZWxldGU6b3JnYW5pemF0aW9uX21lbWJlcnMgY3JlYXRlOm9yZ2FuaXphdGlvbl9jb25uZWN0aW9ucyByZWFkOm9yZ2FuaXphdGlvbl9jb25uZWN0aW9ucyB1cGRhdGU6b3JnYW5pemF0aW9uX2Nvbm5lY3Rpb25zIGRlbGV0ZTpvcmdhbml6YXRpb25fY29ubmVjdGlvbnMgY3JlYXRlOm9yZ2FuaXphdGlvbl9tZW1iZXJfcm9sZXMgcmVhZDpvcmdhbml6YXRpb25fbWVtYmVyX3JvbGVzIGRlbGV0ZTpvcmdhbml6YXRpb25fbWVtYmVyX3JvbGVzIGNyZWF0ZTpvcmdhbml6YXRpb25faW52aXRhdGlvbnMgcmVhZDpvcmdhbml6YXRpb25faW52aXRhdGlvbnMgZGVsZXRlOm9yZ2FuaXphdGlvbl9pbnZpdGF0aW9ucyIsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyJ9.Ds_SxKhYo-txxFfGKWb8_Pyd4ebJsPnPfckvZ6nPbgfum5rTReofuVagjgHzFjmhbHNwW8fb_m7adGwwKqaCgYb-xWaecTXX97A1CSQDAIPejb5gRNqdjybGMYLmuFAl6Xa9hEq_zUWJlZtL2OKZS1YGr1e_KLD7EkDqiYCFHYvs6R1RAcJwcEu9C20kc1NrAVm-KojphUXqc01mtJz0MNQLbflM0uORijAcHFY8I7-Il6grhK7YgbO05WZ4JdSpW6wcF7Buep15SPU1IR0jQuTKuy8z-KGSRd_vKZ_sTu_Od8EuTgGqtIWXOArNkSxEdWKdHJRDloUKlNQIIMWKVA");

                HttpEntity<Object> entity = new HttpEntity<>(newUsersDTO, headers);
                String uri = "https://auth-projects.us.auth0.com/api/v2/users";

                ResponseEntity<NewUsersDTO> response = restTemplate.postForEntity(uri, entity, NewUsersDTO.class);

                if (response.getStatusCode() == HttpStatus.CREATED) {
                    log.info("Creado en Auth0");
                    //Crea registro en BD
                    dataUsers.setAuthid("auth0|2022fff" + formateadorFecha.format(date) + "dbmaq");
                    serviceDataUsers.saveUsers(dataUsers);
                    log.info("Creado en BD");
                    return "redirect:viewUsers?id=" + dataUsers.getId() + "&update=true";
                }

            } catch (Exception ex) {
                log.info("Error en registro: Por favor verificar campo incorrecto");
                boolean conflict = ex.getMessage().contains("409");
                boolean password = ex.getMessage().contains("PasswordStrengthError");
                return "redirect:newUsers?" + "conflict=" + conflict + "&password=" + password;
            }
        }

        if (Objects.equals(type, "update")) {
            try {
                //Crea registro en AUTH0

                String authid = dataUsers.getAuthid();

                EditUsersDTO editUsersDTO = new EditUsersDTO();
                editUsersDTO.setName(dataUsers.getName());
                editUsersDTO.setNickname(dataUsers.getLogin());
                editUsersDTO.setPassword(dataUsers.getPassword());
                editUsersDTO.setConnection("Username-Password-Authentication");
                if (Objects.equals(dataUsers.getState(), "Activo")) {
                    editUsersDTO.setBlocked(false);
                } else {
                    editUsersDTO.setBlocked(true);
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InJxSEU5alBHOXkzU1Z6SDFHU2t4bCJ9.eyJpc3MiOiJodHRwczovL2F1dGgtcHJvamVjdHMudXMuYXV0aDAuY29tLyIsInN1YiI6IkE4N0tlQUVBdVpwQ3hCN0d6clhHSDJKS3UxQUFINVAwQGNsaWVudHMiLCJhdWQiOiJodHRwczovL2F1dGgtcHJvamVjdHMudXMuYXV0aDAuY29tL2FwaS92Mi8iLCJpYXQiOjE2NDgwMDY1MzAsImV4cCI6MTY1MDU5ODUzMCwiYXpwIjoiQTg3S2VBRUF1WnBDeEI3R3pyWEdIMkpLdTFBQUg1UDAiLCJzY29wZSI6InJlYWQ6Y2xpZW50X2dyYW50cyBjcmVhdGU6Y2xpZW50X2dyYW50cyBkZWxldGU6Y2xpZW50X2dyYW50cyB1cGRhdGU6Y2xpZW50X2dyYW50cyByZWFkOnVzZXJzIHVwZGF0ZTp1c2VycyBkZWxldGU6dXNlcnMgY3JlYXRlOnVzZXJzIHJlYWQ6dXNlcnNfYXBwX21ldGFkYXRhIHVwZGF0ZTp1c2Vyc19hcHBfbWV0YWRhdGEgZGVsZXRlOnVzZXJzX2FwcF9tZXRhZGF0YSBjcmVhdGU6dXNlcnNfYXBwX21ldGFkYXRhIHJlYWQ6dXNlcl9jdXN0b21fYmxvY2tzIGNyZWF0ZTp1c2VyX2N1c3RvbV9ibG9ja3MgZGVsZXRlOnVzZXJfY3VzdG9tX2Jsb2NrcyBjcmVhdGU6dXNlcl90aWNrZXRzIHJlYWQ6Y2xpZW50cyB1cGRhdGU6Y2xpZW50cyBkZWxldGU6Y2xpZW50cyBjcmVhdGU6Y2xpZW50cyByZWFkOmNsaWVudF9rZXlzIHVwZGF0ZTpjbGllbnRfa2V5cyBkZWxldGU6Y2xpZW50X2tleXMgY3JlYXRlOmNsaWVudF9rZXlzIHJlYWQ6Y29ubmVjdGlvbnMgdXBkYXRlOmNvbm5lY3Rpb25zIGRlbGV0ZTpjb25uZWN0aW9ucyBjcmVhdGU6Y29ubmVjdGlvbnMgcmVhZDpyZXNvdXJjZV9zZXJ2ZXJzIHVwZGF0ZTpyZXNvdXJjZV9zZXJ2ZXJzIGRlbGV0ZTpyZXNvdXJjZV9zZXJ2ZXJzIGNyZWF0ZTpyZXNvdXJjZV9zZXJ2ZXJzIHJlYWQ6ZGV2aWNlX2NyZWRlbnRpYWxzIHVwZGF0ZTpkZXZpY2VfY3JlZGVudGlhbHMgZGVsZXRlOmRldmljZV9jcmVkZW50aWFscyBjcmVhdGU6ZGV2aWNlX2NyZWRlbnRpYWxzIHJlYWQ6cnVsZXMgdXBkYXRlOnJ1bGVzIGRlbGV0ZTpydWxlcyBjcmVhdGU6cnVsZXMgcmVhZDpydWxlc19jb25maWdzIHVwZGF0ZTpydWxlc19jb25maWdzIGRlbGV0ZTpydWxlc19jb25maWdzIHJlYWQ6aG9va3MgdXBkYXRlOmhvb2tzIGRlbGV0ZTpob29rcyBjcmVhdGU6aG9va3MgcmVhZDphY3Rpb25zIHVwZGF0ZTphY3Rpb25zIGRlbGV0ZTphY3Rpb25zIGNyZWF0ZTphY3Rpb25zIHJlYWQ6ZW1haWxfcHJvdmlkZXIgdXBkYXRlOmVtYWlsX3Byb3ZpZGVyIGRlbGV0ZTplbWFpbF9wcm92aWRlciBjcmVhdGU6ZW1haWxfcHJvdmlkZXIgYmxhY2tsaXN0OnRva2VucyByZWFkOnN0YXRzIHJlYWQ6aW5zaWdodHMgcmVhZDp0ZW5hbnRfc2V0dGluZ3MgdXBkYXRlOnRlbmFudF9zZXR0aW5ncyByZWFkOmxvZ3MgcmVhZDpsb2dzX3VzZXJzIHJlYWQ6c2hpZWxkcyBjcmVhdGU6c2hpZWxkcyB1cGRhdGU6c2hpZWxkcyBkZWxldGU6c2hpZWxkcyByZWFkOmFub21hbHlfYmxvY2tzIGRlbGV0ZTphbm9tYWx5X2Jsb2NrcyB1cGRhdGU6dHJpZ2dlcnMgcmVhZDp0cmlnZ2VycyByZWFkOmdyYW50cyBkZWxldGU6Z3JhbnRzIHJlYWQ6Z3VhcmRpYW5fZmFjdG9ycyB1cGRhdGU6Z3VhcmRpYW5fZmFjdG9ycyByZWFkOmd1YXJkaWFuX2Vucm9sbG1lbnRzIGRlbGV0ZTpndWFyZGlhbl9lbnJvbGxtZW50cyBjcmVhdGU6Z3VhcmRpYW5fZW5yb2xsbWVudF90aWNrZXRzIHJlYWQ6dXNlcl9pZHBfdG9rZW5zIGNyZWF0ZTpwYXNzd29yZHNfY2hlY2tpbmdfam9iIGRlbGV0ZTpwYXNzd29yZHNfY2hlY2tpbmdfam9iIHJlYWQ6Y3VzdG9tX2RvbWFpbnMgZGVsZXRlOmN1c3RvbV9kb21haW5zIGNyZWF0ZTpjdXN0b21fZG9tYWlucyB1cGRhdGU6Y3VzdG9tX2RvbWFpbnMgcmVhZDplbWFpbF90ZW1wbGF0ZXMgY3JlYXRlOmVtYWlsX3RlbXBsYXRlcyB1cGRhdGU6ZW1haWxfdGVtcGxhdGVzIHJlYWQ6bWZhX3BvbGljaWVzIHVwZGF0ZTptZmFfcG9saWNpZXMgcmVhZDpyb2xlcyBjcmVhdGU6cm9sZXMgZGVsZXRlOnJvbGVzIHVwZGF0ZTpyb2xlcyByZWFkOnByb21wdHMgdXBkYXRlOnByb21wdHMgcmVhZDpicmFuZGluZyB1cGRhdGU6YnJhbmRpbmcgZGVsZXRlOmJyYW5kaW5nIHJlYWQ6bG9nX3N0cmVhbXMgY3JlYXRlOmxvZ19zdHJlYW1zIGRlbGV0ZTpsb2dfc3RyZWFtcyB1cGRhdGU6bG9nX3N0cmVhbXMgY3JlYXRlOnNpZ25pbmdfa2V5cyByZWFkOnNpZ25pbmdfa2V5cyB1cGRhdGU6c2lnbmluZ19rZXlzIHJlYWQ6bGltaXRzIHVwZGF0ZTpsaW1pdHMgY3JlYXRlOnJvbGVfbWVtYmVycyByZWFkOnJvbGVfbWVtYmVycyBkZWxldGU6cm9sZV9tZW1iZXJzIHJlYWQ6ZW50aXRsZW1lbnRzIHJlYWQ6YXR0YWNrX3Byb3RlY3Rpb24gdXBkYXRlOmF0dGFja19wcm90ZWN0aW9uIHJlYWQ6b3JnYW5pemF0aW9uc19zdW1tYXJ5IHJlYWQ6b3JnYW5pemF0aW9ucyB1cGRhdGU6b3JnYW5pemF0aW9ucyBjcmVhdGU6b3JnYW5pemF0aW9ucyBkZWxldGU6b3JnYW5pemF0aW9ucyBjcmVhdGU6b3JnYW5pemF0aW9uX21lbWJlcnMgcmVhZDpvcmdhbml6YXRpb25fbWVtYmVycyBkZWxldGU6b3JnYW5pemF0aW9uX21lbWJlcnMgY3JlYXRlOm9yZ2FuaXphdGlvbl9jb25uZWN0aW9ucyByZWFkOm9yZ2FuaXphdGlvbl9jb25uZWN0aW9ucyB1cGRhdGU6b3JnYW5pemF0aW9uX2Nvbm5lY3Rpb25zIGRlbGV0ZTpvcmdhbml6YXRpb25fY29ubmVjdGlvbnMgY3JlYXRlOm9yZ2FuaXphdGlvbl9tZW1iZXJfcm9sZXMgcmVhZDpvcmdhbml6YXRpb25fbWVtYmVyX3JvbGVzIGRlbGV0ZTpvcmdhbml6YXRpb25fbWVtYmVyX3JvbGVzIGNyZWF0ZTpvcmdhbml6YXRpb25faW52aXRhdGlvbnMgcmVhZDpvcmdhbml6YXRpb25faW52aXRhdGlvbnMgZGVsZXRlOm9yZ2FuaXphdGlvbl9pbnZpdGF0aW9ucyIsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyJ9.Ds_SxKhYo-txxFfGKWb8_Pyd4ebJsPnPfckvZ6nPbgfum5rTReofuVagjgHzFjmhbHNwW8fb_m7adGwwKqaCgYb-xWaecTXX97A1CSQDAIPejb5gRNqdjybGMYLmuFAl6Xa9hEq_zUWJlZtL2OKZS1YGr1e_KLD7EkDqiYCFHYvs6R1RAcJwcEu9C20kc1NrAVm-KojphUXqc01mtJz0MNQLbflM0uORijAcHFY8I7-Il6grhK7YgbO05WZ4JdSpW6wcF7Buep15SPU1IR0jQuTKuy8z-KGSRd_vKZ_sTu_Od8EuTgGqtIWXOArNkSxEdWKdHJRDloUKlNQIIMWKVA");

                HttpEntity<Object> entity = new HttpEntity<>(editUsersDTO, headers);
                String uri = "https://auth-projects.us.auth0.com/api/v2/users/" + authid;

                RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

                String response = restTemplate.patchForObject(uri, entity, String.class);

                serviceDataUsers.saveUsers(dataUsers);
                return "redirect:viewUsers?id=" + dataUsers.getId() + "&update=true";

            } catch (Exception ex) {
                log.info("Error en registro: Por favor verificar campo incorrecto");
                boolean conflict = ex.getMessage().contains("409");
                boolean password = ex.getMessage().contains("PasswordStrengthError");
                return "redirect:newUsers?" + "conflict=" + conflict + "&password=" + password;
            }
        }

        return "dataUsers";

    }

    @RequestMapping("/deleteUsers")
    public String deleteUsers(Model model, long id) {
        serviceDataUsers.deleteUsers(id);
        return "redirect:dataUsers";
    }

    @GetMapping("/adminFeatures")
    public String features(Model model) {
        List<DataFeatures> dataFeaturesList = serviceDataFeatures.getAllDataFeatures();
        model.addAttribute("dataFeaturesList", dataFeaturesList);
        return "adminFeatures";
    }

    @GetMapping("/viewFeatures")
    public String viewFeatures(long id, Model model, boolean update) {
        DataFeatures dataFeatures = serviceDataFeatures.getOneDataFeatures(id);
        if (update) {
            model.addAttribute("edit", "Editado");
        }
        model.addAttribute("dataFeatures", dataFeatures);
        return "viewFeatures";
    }

    @RequestMapping("/editFeatures")
    public String editFeatures(Model model, DataFeatures dataFeatures) {
        if (dataFeatures.getModule().equals("Calculadora") || dataFeatures.getModule().equals("calc")) {
            dataFeatures.setModule("calc");
        } else {
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
                                 Long idProduct, boolean deleteProduct) {

        if (idFeature != null) {
            model.addAttribute("id", idFeature);
            model.addAttribute("type", "adminFeatures");
            model.addAttribute("delete", "deleteFeature");
            model.addAttribute("params", "idFeature");
        }

        if (deleteFeature) {
            serviceDataFeatures.deleteDatafeatures(idFeature);
            return "redirect:adminFeatures";
        }

        if (idUsers != null) {
            model.addAttribute("id", idUsers);
            model.addAttribute("type", "dataUsers");
            model.addAttribute("delete", "deleteUsers");
            model.addAttribute("params", "idUsers");
        }

        if (deleteUsers) {

            DataUsers dataUsers = serviceDataUsers.getDataUserById(idUsers);
            String authid = dataUsers.getAuthid();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InJxSEU5alBHOXkzU1Z6SDFHU2t4bCJ9.eyJpc3MiOiJodHRwczovL2F1dGgtcHJvamVjdHMudXMuYXV0aDAuY29tLyIsInN1YiI6IkE4N0tlQUVBdVpwQ3hCN0d6clhHSDJKS3UxQUFINVAwQGNsaWVudHMiLCJhdWQiOiJodHRwczovL2F1dGgtcHJvamVjdHMudXMuYXV0aDAuY29tL2FwaS92Mi8iLCJpYXQiOjE2NDgwMDY1MzAsImV4cCI6MTY1MDU5ODUzMCwiYXpwIjoiQTg3S2VBRUF1WnBDeEI3R3pyWEdIMkpLdTFBQUg1UDAiLCJzY29wZSI6InJlYWQ6Y2xpZW50X2dyYW50cyBjcmVhdGU6Y2xpZW50X2dyYW50cyBkZWxldGU6Y2xpZW50X2dyYW50cyB1cGRhdGU6Y2xpZW50X2dyYW50cyByZWFkOnVzZXJzIHVwZGF0ZTp1c2VycyBkZWxldGU6dXNlcnMgY3JlYXRlOnVzZXJzIHJlYWQ6dXNlcnNfYXBwX21ldGFkYXRhIHVwZGF0ZTp1c2Vyc19hcHBfbWV0YWRhdGEgZGVsZXRlOnVzZXJzX2FwcF9tZXRhZGF0YSBjcmVhdGU6dXNlcnNfYXBwX21ldGFkYXRhIHJlYWQ6dXNlcl9jdXN0b21fYmxvY2tzIGNyZWF0ZTp1c2VyX2N1c3RvbV9ibG9ja3MgZGVsZXRlOnVzZXJfY3VzdG9tX2Jsb2NrcyBjcmVhdGU6dXNlcl90aWNrZXRzIHJlYWQ6Y2xpZW50cyB1cGRhdGU6Y2xpZW50cyBkZWxldGU6Y2xpZW50cyBjcmVhdGU6Y2xpZW50cyByZWFkOmNsaWVudF9rZXlzIHVwZGF0ZTpjbGllbnRfa2V5cyBkZWxldGU6Y2xpZW50X2tleXMgY3JlYXRlOmNsaWVudF9rZXlzIHJlYWQ6Y29ubmVjdGlvbnMgdXBkYXRlOmNvbm5lY3Rpb25zIGRlbGV0ZTpjb25uZWN0aW9ucyBjcmVhdGU6Y29ubmVjdGlvbnMgcmVhZDpyZXNvdXJjZV9zZXJ2ZXJzIHVwZGF0ZTpyZXNvdXJjZV9zZXJ2ZXJzIGRlbGV0ZTpyZXNvdXJjZV9zZXJ2ZXJzIGNyZWF0ZTpyZXNvdXJjZV9zZXJ2ZXJzIHJlYWQ6ZGV2aWNlX2NyZWRlbnRpYWxzIHVwZGF0ZTpkZXZpY2VfY3JlZGVudGlhbHMgZGVsZXRlOmRldmljZV9jcmVkZW50aWFscyBjcmVhdGU6ZGV2aWNlX2NyZWRlbnRpYWxzIHJlYWQ6cnVsZXMgdXBkYXRlOnJ1bGVzIGRlbGV0ZTpydWxlcyBjcmVhdGU6cnVsZXMgcmVhZDpydWxlc19jb25maWdzIHVwZGF0ZTpydWxlc19jb25maWdzIGRlbGV0ZTpydWxlc19jb25maWdzIHJlYWQ6aG9va3MgdXBkYXRlOmhvb2tzIGRlbGV0ZTpob29rcyBjcmVhdGU6aG9va3MgcmVhZDphY3Rpb25zIHVwZGF0ZTphY3Rpb25zIGRlbGV0ZTphY3Rpb25zIGNyZWF0ZTphY3Rpb25zIHJlYWQ6ZW1haWxfcHJvdmlkZXIgdXBkYXRlOmVtYWlsX3Byb3ZpZGVyIGRlbGV0ZTplbWFpbF9wcm92aWRlciBjcmVhdGU6ZW1haWxfcHJvdmlkZXIgYmxhY2tsaXN0OnRva2VucyByZWFkOnN0YXRzIHJlYWQ6aW5zaWdodHMgcmVhZDp0ZW5hbnRfc2V0dGluZ3MgdXBkYXRlOnRlbmFudF9zZXR0aW5ncyByZWFkOmxvZ3MgcmVhZDpsb2dzX3VzZXJzIHJlYWQ6c2hpZWxkcyBjcmVhdGU6c2hpZWxkcyB1cGRhdGU6c2hpZWxkcyBkZWxldGU6c2hpZWxkcyByZWFkOmFub21hbHlfYmxvY2tzIGRlbGV0ZTphbm9tYWx5X2Jsb2NrcyB1cGRhdGU6dHJpZ2dlcnMgcmVhZDp0cmlnZ2VycyByZWFkOmdyYW50cyBkZWxldGU6Z3JhbnRzIHJlYWQ6Z3VhcmRpYW5fZmFjdG9ycyB1cGRhdGU6Z3VhcmRpYW5fZmFjdG9ycyByZWFkOmd1YXJkaWFuX2Vucm9sbG1lbnRzIGRlbGV0ZTpndWFyZGlhbl9lbnJvbGxtZW50cyBjcmVhdGU6Z3VhcmRpYW5fZW5yb2xsbWVudF90aWNrZXRzIHJlYWQ6dXNlcl9pZHBfdG9rZW5zIGNyZWF0ZTpwYXNzd29yZHNfY2hlY2tpbmdfam9iIGRlbGV0ZTpwYXNzd29yZHNfY2hlY2tpbmdfam9iIHJlYWQ6Y3VzdG9tX2RvbWFpbnMgZGVsZXRlOmN1c3RvbV9kb21haW5zIGNyZWF0ZTpjdXN0b21fZG9tYWlucyB1cGRhdGU6Y3VzdG9tX2RvbWFpbnMgcmVhZDplbWFpbF90ZW1wbGF0ZXMgY3JlYXRlOmVtYWlsX3RlbXBsYXRlcyB1cGRhdGU6ZW1haWxfdGVtcGxhdGVzIHJlYWQ6bWZhX3BvbGljaWVzIHVwZGF0ZTptZmFfcG9saWNpZXMgcmVhZDpyb2xlcyBjcmVhdGU6cm9sZXMgZGVsZXRlOnJvbGVzIHVwZGF0ZTpyb2xlcyByZWFkOnByb21wdHMgdXBkYXRlOnByb21wdHMgcmVhZDpicmFuZGluZyB1cGRhdGU6YnJhbmRpbmcgZGVsZXRlOmJyYW5kaW5nIHJlYWQ6bG9nX3N0cmVhbXMgY3JlYXRlOmxvZ19zdHJlYW1zIGRlbGV0ZTpsb2dfc3RyZWFtcyB1cGRhdGU6bG9nX3N0cmVhbXMgY3JlYXRlOnNpZ25pbmdfa2V5cyByZWFkOnNpZ25pbmdfa2V5cyB1cGRhdGU6c2lnbmluZ19rZXlzIHJlYWQ6bGltaXRzIHVwZGF0ZTpsaW1pdHMgY3JlYXRlOnJvbGVfbWVtYmVycyByZWFkOnJvbGVfbWVtYmVycyBkZWxldGU6cm9sZV9tZW1iZXJzIHJlYWQ6ZW50aXRsZW1lbnRzIHJlYWQ6YXR0YWNrX3Byb3RlY3Rpb24gdXBkYXRlOmF0dGFja19wcm90ZWN0aW9uIHJlYWQ6b3JnYW5pemF0aW9uc19zdW1tYXJ5IHJlYWQ6b3JnYW5pemF0aW9ucyB1cGRhdGU6b3JnYW5pemF0aW9ucyBjcmVhdGU6b3JnYW5pemF0aW9ucyBkZWxldGU6b3JnYW5pemF0aW9ucyBjcmVhdGU6b3JnYW5pemF0aW9uX21lbWJlcnMgcmVhZDpvcmdhbml6YXRpb25fbWVtYmVycyBkZWxldGU6b3JnYW5pemF0aW9uX21lbWJlcnMgY3JlYXRlOm9yZ2FuaXphdGlvbl9jb25uZWN0aW9ucyByZWFkOm9yZ2FuaXphdGlvbl9jb25uZWN0aW9ucyB1cGRhdGU6b3JnYW5pemF0aW9uX2Nvbm5lY3Rpb25zIGRlbGV0ZTpvcmdhbml6YXRpb25fY29ubmVjdGlvbnMgY3JlYXRlOm9yZ2FuaXphdGlvbl9tZW1iZXJfcm9sZXMgcmVhZDpvcmdhbml6YXRpb25fbWVtYmVyX3JvbGVzIGRlbGV0ZTpvcmdhbml6YXRpb25fbWVtYmVyX3JvbGVzIGNyZWF0ZTpvcmdhbml6YXRpb25faW52aXRhdGlvbnMgcmVhZDpvcmdhbml6YXRpb25faW52aXRhdGlvbnMgZGVsZXRlOm9yZ2FuaXphdGlvbl9pbnZpdGF0aW9ucyIsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyJ9.Ds_SxKhYo-txxFfGKWb8_Pyd4ebJsPnPfckvZ6nPbgfum5rTReofuVagjgHzFjmhbHNwW8fb_m7adGwwKqaCgYb-xWaecTXX97A1CSQDAIPejb5gRNqdjybGMYLmuFAl6Xa9hEq_zUWJlZtL2OKZS1YGr1e_KLD7EkDqiYCFHYvs6R1RAcJwcEu9C20kc1NrAVm-KojphUXqc01mtJz0MNQLbflM0uORijAcHFY8I7-Il6grhK7YgbO05WZ4JdSpW6wcF7Buep15SPU1IR0jQuTKuy8z-KGSRd_vKZ_sTu_Od8EuTgGqtIWXOArNkSxEdWKdHJRDloUKlNQIIMWKVA");

            String uri = "https://auth-projects.us.auth0.com/api/v2/users/" + authid;
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);

            serviceDataUsers.deleteUsers(idUsers);
            return "redirect:dataUsers";
        }

        if (idTrm != null) {
            model.addAttribute("id", idTrm);
            model.addAttribute("type", "adminTrm");
            model.addAttribute("delete", "deleteTrm");
            model.addAttribute("params", "idTrm");
        }

        if (deleteTrm) {
            serviceDataCalc.deleteDatacalc(idTrm);
            return "redirect:adminTrm";
        }

        if (idProduct != null) {
            model.addAttribute("id", idProduct);
            model.addAttribute("type", "adminProducts");
            model.addAttribute("delete", "deleteProduct");
            model.addAttribute("params", "idProduct");
        }

        if (deleteProduct) {
            //Delete Product - Service
            return "redirect:adminProducts";
        }

        return "confirmDelete";

    }

    @RequestMapping("/newFeatures")
    public String newFeatures(Model model, DataFeatures dataFeatures) {
        List<String> dataFeaturesDistinctMenu = serviceDataFeatures.getDistinctMenu();
        List<String> dataFeaturesDistinctKeyMenu = serviceDataFeatures.getDistinctKeyMenu();
        List<DataFeatures> dataFeaturesList = serviceDataFeatures.getAllDataFeatures();
        model.addAttribute("dataFeaturesDistinctMenu", dataFeaturesDistinctMenu);
        model.addAttribute("dataFeaturesDistinctKeyMenu", dataFeaturesDistinctKeyMenu);
        model.addAttribute("dataFeaturesList", dataFeaturesList);
        return "viewFeatures";
    }

    @RequestMapping("/dataClients")
    public String newFeatures(Model model) {
        List<DataClients> dataClientsList =  serviceDataClients.getDataClients();
        model.addAttribute("dataClients", dataClientsList);
        return "adminClients";
    }

    @RequestMapping("/newClients")
    public String newClients(Model model) {

        return "viewClients";
    }


}
