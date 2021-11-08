package com.server.calc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataCastResult {

    private String valueCopCast;
    private String legalizationCast;

    private String vipCast;
    private String distributorCast;
    private String consumerCast;
    private String pricePublicCast;

}
