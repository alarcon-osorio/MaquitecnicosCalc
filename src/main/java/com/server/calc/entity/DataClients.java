package com.server.calc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "dataclients")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DataClients {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
