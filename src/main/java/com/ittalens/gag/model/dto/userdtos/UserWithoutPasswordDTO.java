package com.ittalens.gag.model.dto.userdtos;

import lombok.Data;

@Data
public class UserWithoutPasswordDTO {
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String userName;
    private int age;
}
