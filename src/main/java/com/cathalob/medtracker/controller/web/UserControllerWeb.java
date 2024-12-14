package com.cathalob.medtracker.controller.web;

import com.cathalob.medtracker.err.PractitionerRoleRequestNotFound;
import com.cathalob.medtracker.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j

public class UserControllerWeb {
    private final UserServiceImpl userService;

    @GetMapping("/user/practitionerRoleRequest")
    public String practitionerRoleRequest(Model model, Authentication authentication) {
        try {
            userService.getPractitionerRoleRequest(authentication.getName()).ifPresent(practitionerRoleRequest -> {
                model.addAttribute("pending", practitionerRoleRequest.isPending());
                model.addAttribute("approved", practitionerRoleRequest.isApproved());
                model.addAttribute("notSubmitted", (false));
            });
        } catch (PractitionerRoleRequestNotFound practitionerRoleRequestNotFound) {
            model.addAttribute("pending", false);
            model.addAttribute("approved", false);
            model.addAttribute("notSubmitted", (true));
        }
        return "user/practitionerRoleRequest";
    }

    @GetMapping("/user/accountManagement")
    public String getAccountManagement() {
        return "user/accountManagement";
    }

    @PostMapping("/user/accountManagement/practitionerRoleRequest")
    public String practitionerRoleRequest(Authentication authentication) {
        return "redirect:/user/practitionerRoleRequest?" + (userService.submitPractitionerRoleRequest(authentication.getName()) ? "success" : "error");
    }

    @PostMapping("/user/accountManagement/change_password")
    public String passwordChangeRequest(Authentication authentication) {
        return "redirect:/user/accountManagement?" + (userService.submitPasswordChangeRequest() ? "success" : "error");
    }

}
