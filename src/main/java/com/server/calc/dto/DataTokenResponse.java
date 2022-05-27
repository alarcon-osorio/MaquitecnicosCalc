package com.server.calc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataTokenResponse {

    @Id
    String access_token;
    String scope;
    int expires_in;
    String token_type;

}
