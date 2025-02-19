package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.model.UserModel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;


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

    public void sendEmailWithAttachments(String toAddress, String subject, String text, HashMap<String, ByteArrayResource> attachments)
            throws MailException, MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);


        helper.setFrom(fromAddress());
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(text);
        attachments.forEach((s, byteArrayResource) -> {

            try {
                helper.addAttachment(s, byteArrayResource);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }

        });


        javaMailSender.send(mimeMessage);
    }


    private static String fromAddress() {
        return "medtrackerdemo2025@gmail.com";
    }
}