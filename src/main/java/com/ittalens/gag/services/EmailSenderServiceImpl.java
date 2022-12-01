package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.userdtos.UserWithoutPasswordDTO;
import com.ittalens.gag.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class EmailSenderServiceImpl implements IEmailSenderService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ConfigPropertiesService configPropertiesService;

    @Override
    public void sendVerificationEmail(User user) {

        new Thread(() -> {
            String verifyURL = "http://localhost:" + configPropertiesService.getServerPort() + "/users/verify/" + user.getVerificationCode();
            String subject = "Please verify your registration";
            String senderName = "Nine GAG service";
            String mailContent = "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",<p>";
            mailContent += "<p>Please click the link below to verify your registration: <p>";
            mailContent += "<p>" + verifyURL + "<p>";
            mailContent += "<p> Thank you very much! <p>";

            sendEmail(subject, senderName, mailContent, user.getEmail());
        }).start();
    }

    @Override
    public void sendSuccessfulUploadPost(UserWithoutPasswordDTO user) {

        new Thread(() -> {
            String subject = "Successful upload";
            String senderName = "Nine GAG service";
            String mailContent = "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",<p>";
            mailContent += "<p>Successful upload post! <p>";
            mailContent += "<p> Thank you very much! <p>";

            sendEmail(subject, senderName, mailContent, user.getEmail());
        }).start();
    }

    @Override
    public void sendBlockingUser(User user) {

        new Thread(() -> {
            String subject = "Wrong password";
            String senderName = "Nine GAG service";
            String mailContent = "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",<p>";
            mailContent += "<p>You have entered more than three wrong passwords!<p>";
            mailContent += "<p>Your account has been blocked!<p>";

            sendEmail(subject, senderName, mailContent, user.getEmail());
        }).start();
    }

    private void sendEmail(String subject, String senderName, String mailContent, String email) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom("noreplynninegagservice@gmail.com", senderName);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(mailContent, true);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Email failed!");
        }
        mailSender.send(message);
    }
}
