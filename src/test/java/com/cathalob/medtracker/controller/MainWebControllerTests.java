package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.service.impl.CustomUserDetailsService;
import com.cathalob.medtracker.service.api.impl.JwtServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = MainWebController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {}))
//SecurityConfig.class, JwtAuthenticationFilter.class, JwtServiceImpl.class
class MainWebControllerTests {


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtServiceImpl jwtService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldAllow_index_accessForAnonymousUser() throws Exception {

        this.mockMvc
                .perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));

    }

    @Test
    void shouldAllow_about_accessForAnonymousUser() throws Exception {

        this.mockMvc
                .perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("about"));

    }
}