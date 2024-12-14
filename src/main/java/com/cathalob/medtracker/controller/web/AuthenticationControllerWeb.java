package com.cathalob.medtracker.controller.web;

import com.cathalob.medtracker.dto.UserModelDTO;
import com.cathalob.medtracker.service.web.AuthenticationServiceWeb;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthenticationControllerWeb {
    private final AuthenticationServiceWeb authenticationServiceWeb;
    @GetMapping("/login")
    public String getLoginPage() {
        return "login_page";
    }

    @GetMapping("/registration")
    public String getRegistrationPage(Model model) {
        model.addAttribute("user", new UserModelDTO());
        return "registration_page";
    }

    @PostMapping("/registration")
    public String registerUser(@ModelAttribute @Valid UserModelDTO user) {
        log.info("registration post");
        authenticationServiceWeb.register(user);
        return "redirect:/login_page?success";
    }
}
