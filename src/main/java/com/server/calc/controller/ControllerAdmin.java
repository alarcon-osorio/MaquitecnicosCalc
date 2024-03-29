package com.server.calc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.calc.dto.*;
import com.server.calc.entity.*;
import com.server.calc.exporter.ExcelExportUnlisted;
import com.server.calc.repository.RepositoryDataUsers;
import com.server.calc.service.*;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.server.calc.constants.values.*;

@Controller
@Log4j2
@Component
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

    @Value("${spring.security.oauth2.client.registration.auth0.client-id}")
    private String token_clientId;

    @Value("${spring.security.oauth2.client.registration.auth0.client-secret}")
    private String token_clientSecret;

    @Value("${token.url}")
    private String token_url;

    @Value("${token.grant_type}")
    private String token_grant_type;

    @Value("${uri.get_users}")
    private String uri_get_users;

    @Value("${token.audience}")
    private String token_audience;

    @Value("${datauser.default.password}")
    private static String datauser_default_password;

    @Value("${datauser.default.profile}")
    private static String datauser_default_profile;

    @Value("${datauser.default.state}")
    private static String datauser_default_state;

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
                    return "admin/admin";
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
        return "admin/adminUnlisted";
    }

    @GetMapping("export/excel")
    public void exportToExcel(HttpServletResponse response, String fechaIni, String fechaFin) throws IOException {
        log.info("Fecha Inicio: " + fechaIni);
        log.info("Fecha Inicio: " + fechaFin);
        log.info(response);
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=no_cotizados" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<DataRegistry> dataRegistries = serviceDataRegistry.getDataRegistryByDate(fechaIni, fechaFin);

        ExcelExportUnlisted excelExporter = new ExcelExportUnlisted(dataRegistries);

        excelExporter.export(response);
    }

    @GetMapping("/adminTrm")
    public String trm(Model model) {
        List<DataCalcJoin> trmList = serviceDataTrm.getDataTrmJoinList();
        log.info("Lista: " + trmList);
        model.addAttribute("trmList", trmList);
        return "admin/adminTrm";
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
        return "admin/adminProducts";
    }

    @RequestMapping(value = "/dataUsers")
    public String getUsersAuth(Model model) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String token = getToken().getAccess_token();

        headers.add(HEADER_NAME, HEADER_VALUE + token);
        HttpEntity<String> entity = new HttpEntity<String>(BODY_PARAMETER, headers);
        ResponseEntity<String> response = (new RestTemplate()).exchange(uri_get_users, HttpMethod.GET, entity, String.class);

        UsersDTO[] usersDTO = mapper.readValue(response.getBody(), UsersDTO[].class);

        for (UsersDTO usersAdd : usersDTO) {
            DataUsers userExist = serviceDataUsers.getByEmail(usersAdd.getEmail());
            if (userExist == null) {
                String name = usersAdd.getName();
                String email = usersAdd.getEmail();
                String login = usersAdd.getNickname();
                String password = datauser_default_password;
                String profile = datauser_default_profile;
                String state = datauser_default_state;
                String authid = usersAdd.getUser_id();
                serviceDataUsers.addUsersIdempotente(name, email, login, password, profile, state, authid);
            }
        }

        List<DataUsers> usersList = serviceDataUsers.getAllUsers();

        model.addAttribute("usuarios", usersList);
        return "admin/adminUsers";
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
        return "admin/views/viewUsers";
    }

    @RequestMapping("/newUsers")
    public String newUsers(Model model, DataUsers dataUsers, boolean password, boolean conflict) {
        if (password) {
            model.addAttribute("password", "Err password");
        }

        if (conflict) {
            model.addAttribute("conflict", "Err Email");
        }

        return "admin/views/viewUsers";
    }

    @RequestMapping("/saveUsers")
    public String saveUsers(Model model, DataUsers dataUsers, String type) {

        if (Objects.equals(type, "add")) {
            try {
                //Crea registro en AUTH0
                Date date = new Date();
                SimpleDateFormat hourFormat = new SimpleDateFormat(DATE_FORMAT_HOUR);
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_ONLY_DATE);

                String dateId = dateFormat.format(date);
                String hourId = hourFormat.format(date);

                NewUsersDTO newUsersDTO = getNewUsersDTO(dataUsers, dateId, hourId);

                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

                String token = getToken().getAccess_token();

                headers.add(HEADER_NAME, HEADER_VALUE + token );

                HttpEntity<Object> entity = new HttpEntity<>(newUsersDTO, headers);
                String uri = uri_get_users;

                log.info(entity.getBody());

                ResponseEntity<NewUsersDTO> response = restTemplate.postForEntity(uri, entity, NewUsersDTO.class);

                log.info(response.getStatusCode());

                if (response.getStatusCode() == HttpStatus.CREATED) {
                    log.info("Creado en Auth0");
                    //Crea registro en BD
                    dataUsers.setAuthid("auth0|" + dateId + "calc" + hourId + "mt");
                    serviceDataUsers.saveUsers(dataUsers);
                    log.info("Creado en BD");
                    return "redirect:viewUsers?id=" + dataUsers.getId() + "&update=true";
                }

            } catch (Exception ex) {
                log.info("Error en registro: Por favor verificar campo incorrecto: " + ex);
                boolean conflict = ex.getMessage().contains("409");
                boolean password = ex.getMessage().contains("PasswordStrengthError");
                return "redirect:newUsers?" + "conflict=" + conflict + "&password=" + password;
            }
        }

        if (Objects.equals(type, "update")) {
            try {
                //Crea registro en AUTH0
                String authid = dataUsers.getAuthid();

                EditUsersDTO editUsersDTO = getEditUsersDTO(dataUsers);

                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

                String token = getToken().getAccess_token();

                headers.add(HEADER_NAME, HEADER_VALUE + token );

                HttpEntity<Object> entity = new HttpEntity<>(editUsersDTO, headers);
                String uri = uri_get_users + "/" + authid;

                RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

                String response = restTemplate.patchForObject(uri, entity, String.class);

                serviceDataUsers.saveUsers(dataUsers);
                return "redirect:viewUsers?id=" + dataUsers.getId() + "&update=true";

            } catch (Exception ex) {
                log.info("Error en registro: Por favor verificar campo incorrecto: " + ex);
                boolean conflict = ex.getMessage().contains("409");
                boolean password = ex.getMessage().contains("PasswordStrengthError");
                return "redirect:newUsers?" + "conflict=" + conflict + "&password=" + password;
            }
        }

        return "dataUsers";

    }

    @NotNull
    private static EditUsersDTO getEditUsersDTO(@NotNull DataUsers dataUsers) {
        EditUsersDTO editUsersDTO = new EditUsersDTO();
        editUsersDTO.setName(dataUsers.getName());
        editUsersDTO.setNickname(dataUsers.getLogin());
        editUsersDTO.setPassword(dataUsers.getPassword());
        editUsersDTO.setConnection(CONECT_BD_AUTH0);
        if (Objects.equals(dataUsers.getState(), datauser_default_state)) {
            editUsersDTO.setBlocked(false);
        } else {
            editUsersDTO.setBlocked(true);
        }
        return editUsersDTO;
    }

    private static @NotNull NewUsersDTO getNewUsersDTO(@NotNull DataUsers dataUsers, String dateId, String hourId) {
        NewUsersDTO newUsersDTO = new NewUsersDTO();
        newUsersDTO.setName(dataUsers.getName());
        newUsersDTO.setEmail(dataUsers.getEmail());
        newUsersDTO.setNickname(dataUsers.getLogin());
        newUsersDTO.setUsername(dataUsers.getLogin());
        newUsersDTO.setPassword(dataUsers.getPassword());
        newUsersDTO.setVerify_email(true);
        newUsersDTO.setUser_id(dateId + "calc" + hourId + "mt");
        newUsersDTO.setConnection(CONECT_BD_AUTH0);
        return newUsersDTO;
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

            String token = getToken().getAccess_token();

            headers.add("Authorization", "Bearer " + token);

            String uri = uri_get_users + "/" + authid;
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

    @GetMapping("/viewClients")
    public String viewClients(long id, Model model, String operation) {
        DataClients dataClients = serviceDataClients.getOneDataClients(id);
        if (operation == "edit") {
            model.addAttribute("edit", "Editado");
        }
        if (operation == "exist") {
            model.addAttribute("exist", "Existe");
        }
        model.addAttribute("dataClients", dataClients);
        return "viewClients";
    }

    @RequestMapping("/newClients")
    public String newClients(Model model, DataClients dataClients, String operation) {
        if (operation == "add"){
            model.addAttribute("add", "Agregado");
            return "viewClients";
        }

        return "viewClients";
    }

    @RequestMapping("/saveClients")
    public String saveClients(Model model, DataClients dataClients) {

        if(dataClients.getId() == null ){
            serviceDataClients.saveDataClients(dataClients);
            return "redirect:dataClients";
        }else{
            dataClients.setNit(dataClients.getNit());
            dataClients.setCompany(dataClients.getCompany());
            dataClients.setEmail(dataClients.getEmail());
            dataClients.setClientname(dataClients.getClientname());
            dataClients.setCellphone(dataClients.getCellphone());
            dataClients.setAddress(dataClients.getAddress());
            dataClients.setWeb(dataClients.getWeb());
            dataClients.setType(dataClients.getType());
            serviceDataClients.saveDataClients(dataClients);
        }

       return "redirect:viewClients?id=" + dataClients.getId() + "&operation=edit";

    }

    @RequestMapping("/editClients")
    public String editClients(Model model, DataClients dataClients) {
        serviceDataClients.updateDataClients(dataClients);
        return "redirect:viewClients?id=" + dataClients.getNit() + "&update=true";
    }

    public DataTokenResponse getToken (){

        String URI_TOKEN = token_url;

        DataToken dataToken = new DataToken();
        dataToken.setClient_id(token_clientId);
        dataToken.setGrant_type(token_grant_type);
        dataToken.setClient_secret(token_clientSecret);
        dataToken.setAudience(token_audience);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<DataToken> entity = new HttpEntity<DataToken>(dataToken,headers);

        ResponseEntity<DataTokenResponse> response = restTemplate.exchange(
                URI_TOKEN, HttpMethod.POST, entity, DataTokenResponse.class);

        log.info(response.getBody());

        return response.getBody();

    }


}
