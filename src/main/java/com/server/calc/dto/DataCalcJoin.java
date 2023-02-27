package com.server.calc.dto;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class DataCalcJoin {
        @Id
        private long id;
        @Column(name ="iddatastatic")
        private long idDatastatic;
        private String concept;
        private long valueCop;
        private long legalization;
        private long position;

}
