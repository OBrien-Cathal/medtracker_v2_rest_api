package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.service.impl.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationTestController {
    private final MailService mailService;
    private final UserService userService;

    @GetMapping("/send-test-mail")
    public String send() {

//        userService.findByLogin()

        UserModel userModel = new UserModel();
        userModel.setUsername("medtrackerdemo2025@gmail.com");
        try {


            mailService.sendEmail(userModel);
        } catch (MailException mailException) {
            System.out.println(mailException);
        }
        return "Your mail has been sent to the user with email: " + userModel.getUsername();

    }
}
