package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.controller.AdminControllerApi;
import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.service.impl.AuthenticationServiceApi;
import com.cathalob.medtracker.service.impl.JwtServiceImpl;
import com.cathalob.medtracker.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = AdminControllerApi.class)
class AdminControllerApiTests {
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtServiceImpl jwtService;
    @MockBean
    private AuthenticationServiceApi authenticationServiceApi;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;



}