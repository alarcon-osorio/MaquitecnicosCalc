package com.server.calc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "datatemp")
public class DataTemp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String imports;
    private String ref;
    private String descript;
    private long cant;
    private String vip;
    private String distri;
    private String consu;
    private String pub;

}
