package com.example.demo.controller;

import com.example.demo.auth.service.JwtService;
import com.example.demo.auth.service.UserDetailsServiceImpl;
import com.example.demo.entity.Tag;
import com.example.demo.repository.TagRepository;
import com.example.demo.service.IUtilService;
import com.example.demo.service.impl.TagServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
@WithMockUser(username = "user", password = "test", roles = {"USER", "MOD", "ADMIN"})
@AutoConfigureMockMvc(addFilters = false)
public class TagControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TagServiceImpl tagService;
    @MockBean
    private TagRepository tagRepository;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void testGetTags() throws Exception {
        given(tagService.getTags()).willReturn(Collections.singletonList(new Tag()));

        RequestBuilder requestBuilder= MockMvcRequestBuilders
                .get("/tags")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
    }
}
