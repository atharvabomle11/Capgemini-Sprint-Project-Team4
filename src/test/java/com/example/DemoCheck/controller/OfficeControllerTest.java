package com.example.DemoCheck.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OfficeRestApiTest {

    @Autowired
    private MockMvc mockMvc;

    //pass case
    @Test
    void getAllOffices_shouldReturnData() throws Exception {

        mockMvc.perform(get("/offices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.offices.length()").isNotEmpty())

                .andExpect(jsonPath("$._embedded.offices[0].city").exists())
                .andExpect(jsonPath("$._embedded.offices[0].phone").exists())
                .andExpect(jsonPath("$._embedded.offices[0].country").exists());;
    }

    //pass case
    @Test
    void getOfficeByOfficeCode_shouldReturnOffice_whenOfficeCodeExists() throws Exception {

        mockMvc.perform(get("/offices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").exists())
                .andExpect(jsonPath("$.phone").exists())
                .andExpect(jsonPath("$.city").exists())
                .andExpect(jsonPath("$._links.self.href").exists());
    }
    //fail case
    @Test
    void getOfficeByOfficeCode_shouldReturnOffice_whenOfficeDoesCodeExists() throws Exception {

        mockMvc.perform(get("/offices/10"))
                .andExpect(status().isNotFound());
    }
}