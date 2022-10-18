package com.ittalens.gag.model.dto.userdtos;

import lombok.Data;

@Data
public class EditUserDTO {
    private String firstName;
    private String lastName;
    private int age;
    private String userName;
    private String email;
}
