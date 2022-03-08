package com.server.calc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dataregistry")
public class DataRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    public String datereg;
    private long refoem;
    private String refsadt;
    private long cant;
    private String reason;
    private String cop;
    private long cantsug;
    private String brand;
    public String user;
    public String client;
    public String obs;

}
