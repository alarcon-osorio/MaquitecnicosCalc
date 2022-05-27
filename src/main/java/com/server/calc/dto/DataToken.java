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
public class DataToken {

    @Id
    String client_id;

    String grant_type;
    String client_secret;
    String audience;

}
