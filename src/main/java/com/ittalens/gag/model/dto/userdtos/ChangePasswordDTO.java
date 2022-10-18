package com.ittalens.gag.model.dto.userdtos;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
}
