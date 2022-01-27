package com.server.calc.dto;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class DataCalcJoin {

        @Id
        private Long id;
        private String concept;
        private Long valueCop;
        private Long legalization;

}
