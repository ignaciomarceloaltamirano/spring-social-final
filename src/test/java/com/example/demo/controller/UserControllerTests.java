package com.example.demo.controller;

import com.example.demo.dto.request.UpdatePasswordRequestDto;
import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.auth.service.JwtService;
import com.example.demo.auth.service.UserDetailsServiceImpl;
import com.example.demo.dto.request.UpdateUserRequestDto;
import com.example.demo.dto.response.UpdateUserResponseDto;
import com.example.demo.dto.response.UserProfileResponseDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@WithMockUser(username = "user", password = "test", roles = {"USER", "MOD", "ADMIN"})
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ModelMapper modelMapper;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void testGetUsers() throws Exception {
        given(userService.getUsers())
                .willReturn(List.of(new UserProfileResponseDto()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUser() throws Exception {
        given(userService.getUser(anyString())).willReturn(new UserProfileResponseDto());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/{username}","test")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUser_WhenUserNotFound_ThrowsUsernameNotFoundException() throws Exception {
        given(userService.getUser(anyString())).willThrow(
                new ResourceNotFoundException("User not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/{username}","test")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdatePassword_Success() throws Exception {
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto();
        requestDto.setCurrentPassword("oldPassword");
        requestDto.setNewPassword("newPassword");
        requestDto.setConfirmationPassword("newPassword");

        given(userService.updatePassword(requestDto))
                .willReturn(new MessageDto("Password updated."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/users/update-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated."));
    }

    @Test
    public void testUpdatePassword_WhenPasswordIsWrong_ThrowsIllegalStateException() throws Exception {
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto();
        requestDto.setCurrentPassword("wrongPassword");
        requestDto.setNewPassword("newPassword");
        requestDto.setConfirmationPassword("newPassword");

        given(userService.updatePassword(any(UpdatePasswordRequestDto.class)))
                .willThrow(new IllegalStateException("Password is wrong."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/users/update-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdatePassword_WhenPasswordsDoNotMatch_ThrowsIllegalStateException() throws Exception {
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto();
        requestDto.setCurrentPassword("oldPassword");
        requestDto.setNewPassword("newPassword");
        requestDto.setConfirmationPassword("mismatchedPassword");

        given(userService.updatePassword(any(UpdatePasswordRequestDto.class)))
                .willThrow(new IllegalStateException("Passwords don't match."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/users/update-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUser_WithImage() throws Exception {
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto();
        requestDto.setUsername("newUsername");
        requestDto.setEmail("newEmail@example.com");

        String userDtoJson=objectMapper.writeValueAsString(requestDto);

        given(userService.updateUser(any(UpdateUserRequestDto.class), any(MultipartFile.class)))
                .willReturn(new UpdateUserResponseDto());

        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "some image content".getBytes());
        MockMultipartFile jsonFile = new MockMultipartFile("user", "", "application/json", userDtoJson.getBytes());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(HttpMethod.PUT,"/users/update")
                .file(file)
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateUser_WithoutImage() throws Exception {
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto();
        requestDto.setUsername("newUsername");
        requestDto.setEmail("newEmail@example.com");

        String userDtoJson=objectMapper.writeValueAsString(requestDto);

        given(userService.updateUser(requestDto, null))
                .willReturn(new UpdateUserResponseDto());

        MockMultipartFile jsonFile = new MockMultipartFile("user", "", "application/json", userDtoJson.getBytes());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(HttpMethod.PUT,"/users/update")
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }
}
