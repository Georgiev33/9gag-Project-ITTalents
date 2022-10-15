package com.ittalens.gag.model.dto;

import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public class UserDTO {
    private int id;
    private String email;
    private String firstName;
    private String lastName;
}
