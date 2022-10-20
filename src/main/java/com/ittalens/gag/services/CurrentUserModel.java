package com.ittalens.gag.services;

import lombok.Data;

@Data
public class CurrentUserModel {

    private Long id;
    private String userName;

    public CurrentUserModel(Long id, String userName) {
        this.id = id;
        this.userName = userName;
    }
}
