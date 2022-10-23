package com.ittalens.gag.services;

import com.ittalens.gag.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String verifyURL = "https://localhost:8080/users/" + user.getVerificationCode();
        String subject = "Please verify your registration";
        String senderName = "Nine GAG service";
        String mailContent = "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",<p>";
        mailContent += "<p>Please click the link below to verify your registration: <p>";
        mailContent += "<p>" + verifyURL + "<p>";
        mailContent += "<p> Thank you very much! <p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("noreply9GAGservice@gmail.com", senderName);
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.setText(mailContent, true);

        mailSender.send(message);
    }
}
