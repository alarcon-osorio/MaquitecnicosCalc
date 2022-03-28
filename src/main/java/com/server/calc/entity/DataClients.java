package com.server.calc.entity;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "dataclients")
@Data
public class DataClients {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nit;
    private String company;
    private String email;
    private String clientname;
    private String cellphone;
    private String address;
    private String web;
    private String type;


}
