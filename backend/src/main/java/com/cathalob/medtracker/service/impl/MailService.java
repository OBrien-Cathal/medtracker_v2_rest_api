package com.cathalob.medtracker.service.impl;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.cathalob.medtracker.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
        mail.setFrom("medtrackerdemo2025@gmail.com");
        mail.setTo(user.getUsername());
        mail.setSubject("MedTracker Notification");
        mail.setText("Please confirm your registration by clicking on this link\n" +
                "http.....");

        /*
         * This send() contains an Object of SimpleMailMessage as an Parameter
         */
        javaMailSender.send(mail);
    }

    /**
     * This fucntion is used to send mail that contains an attachment.
     *
     * @param user
     * @throws MailException
     * @throws MessagingException
     */
//    public void sendEmailWithAttachment(UserModel user) throws MailException, MessagingException {
//
//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//
//        helper.setTo(user.getUsername());
//        helper.setSubject("Testing Mail API with Attachment");
//        helper.setText("Please find the attached document below.");
//
//        ClassPathResource classPathResource = new ClassPathResource("Attachment.pdf");
//        helper.addAttachment(classPathResource.getFilename(), classPathResource);
//
//        javaMailSender.send(mimeMessage);
//    }

}