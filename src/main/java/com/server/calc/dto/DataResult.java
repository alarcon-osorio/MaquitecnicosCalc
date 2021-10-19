package com.server.calc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataResult {

    private float vip;
    private float distributor;
    private float consumer;
    private float pricePublic;
}
