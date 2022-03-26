package com.server.calc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsersDTO implements Serializable {

    @Id
    private String email;
    private String name;
    private String nickname;
    private String password;
    private String profile;
    private String state;
    private String user_id;

}
