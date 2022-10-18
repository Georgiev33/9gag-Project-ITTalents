package com.ittalens.gag.model.dto.userdtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterUserDTO {
    private long id;
    private String firstName;
    private String lastName;
    private int age;
    private String userName;
    private String email;
    private String password;
    private String repeatedPassword;
    private LocalDateTime registerDate;
    private boolean isActive;
}
