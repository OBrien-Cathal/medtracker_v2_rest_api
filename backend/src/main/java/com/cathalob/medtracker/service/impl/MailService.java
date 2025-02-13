package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;



@Service
public class MailService {
    private JavaMailSender javaMailSender;

    @Autowired
    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }



    public void sendEmail(UserModel user) throws MailException {

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(fromAddress());
        mail.setTo(user.getUsername());
        mail.setSubject("MedTracker Notification");
        mail.setText("Please confirm your registration by clicking on this link\n" +
                "http.....");
        javaMailSender.send(mail);
    }

    public void sendEmail(String toAddress, String subject, String text) throws MailException {


        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(fromAddress());
        mail.setTo(toAddress);
        mail.setSubject(subject);
        mail.setText(text);

        javaMailSender.send(mail);
    }

    private static String fromAddress() {
        return "medtrackerdemo2025@gmail.com";
    }
}