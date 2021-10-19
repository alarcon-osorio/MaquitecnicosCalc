package com.server.calc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "datacalc")
public class DataCalc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long valueCop;
    private long legalization;

}
