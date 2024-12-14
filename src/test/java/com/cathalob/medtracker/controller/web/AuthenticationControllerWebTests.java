package com.cathalob.medtracker.controller.web;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.service.api.impl.JwtServiceImpl;
import com.cathalob.medtracker.service.impl.CustomUserDetailsService;
import com.cathalob.medtracker.service.web.AuthenticationServiceWeb;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = AuthenticationControllerWeb.class)
class AuthenticationControllerWebTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtServiceImpl jwtService;
    @MockBean
    private AuthenticationServiceWeb authenticationServiceWeb;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldAllow_about_accessForAnonymousUser() throws Exception {

        this.mockMvc
                .perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login_page"));
    }

    @Test
    void shouldAllow_index_accessForAnonymousUser() throws Exception {

        this.mockMvc
                .perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration_page"));

    }


}