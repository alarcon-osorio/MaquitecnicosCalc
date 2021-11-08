package com.server.calc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dataproduct")
public class DataProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long importId;
    private long dataTypeId;
    private String reference;
    private String description;
    private double valueDollar;
    private long amount;


}


