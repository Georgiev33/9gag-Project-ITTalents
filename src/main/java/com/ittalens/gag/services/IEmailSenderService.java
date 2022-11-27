package com.ittalens.gag.services;

import com.ittalens.gag.model.entity.User;

public interface IEmailSenderService {

    void sendVerificationEmail(User user);
}
