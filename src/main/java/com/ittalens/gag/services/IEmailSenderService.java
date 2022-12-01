package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.userdtos.UserWithoutPasswordDTO;
import com.ittalens.gag.model.entity.User;

public interface IEmailSenderService {

    void sendVerificationEmail(User user);

    void sendSuccessfulUploadPost(UserWithoutPasswordDTO user);

    void sendBlockingUser(User user);
}
