package com.server.calc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "datafeatures")
public class DataFeatures {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String menu;
    private String module;
    private String description;
    private String value;
    private long menunumber;
    private String keymenu;
}
