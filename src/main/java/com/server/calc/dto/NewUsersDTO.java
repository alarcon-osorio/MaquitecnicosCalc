package com.server.calc.dto;

import lombok.Data;

import javax.persistence.Id;

@Data
public class NewUsersDTO {

    @Id
    private String email;
    private String name;
    private String nickname;
    private String username;
    private String password;
    private boolean verify_email;
    private String user_id;
    private String connection;

}
