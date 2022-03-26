package com.server.calc.dto;

import lombok.Data;

import javax.persistence.Id;

@Data
public class EditUsersDTO {

    @Id
    private String name;
    private String nickname;
    private String password;
    private String connection;
    private boolean blocked;

}
