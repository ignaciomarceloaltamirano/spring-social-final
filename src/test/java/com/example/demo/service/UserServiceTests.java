package com.example.demo.service;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.UpdatePasswordRequestDto;
import com.example.demo.dto.request.UpdateUserRequestDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.UpdateUserResponseDto;
import com.example.demo.dto.response.UserProfileResponseDto;
import com.example.demo.dto.response.UserResponseDto;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private IUtilService utilService;
    @Mock
    private IFileUploadService fileUploadService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testGetUsers() {
        User user1 = User.builder()
                .id(1L)
                .username("user1")
                .email("test1@test.com")
                .password("test")
                .build();

        User user2 = User.builder()
                .id(1L)
                .username("user2")
                .email("test2@test.com")
                .password("test")
                .build();

        given(userRepository.findAll()).willReturn(List.of(user1, user2));

        List<UserProfileResponseDto> result = userService.getUsers();

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(List.class);
        assertThat(result.size()).isEqualTo(2);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUser_Success() {
        User user1 = User.builder()
                .id(1L)
                .username("user1")
                .email("test1@test.com")
                .password("test")
                .build();

        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user1));

        UserProfileResponseDto result = userService.getUser(anyString());

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(UserProfileResponseDto.class);
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    void testGetUser_WhenUserNotFound_ThrowsResourceNotFoundException() {
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.getUser(anyString()));

        assertThat(exception.getMessage()).contains("User not found");
    }

    @Test
    void testUpdatePassword_WhenPasswordsMatch_UpdatesPassword() {
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto();
        requestDto.setCurrentPassword("oldPassword");
        requestDto.setNewPassword("newPassword");
        requestDto.setConfirmationPassword("newPassword");

        User user = new User();
        user.setPassword(passwordEncoder.encode("oldPassword"));

        given(utilService.getCurrentUser()).willReturn(user);
        given(passwordEncoder.matches("oldPassword", user.getPassword())).willReturn(true);

        MessageDto result = userService.updatePassword(requestDto);

        assertThat(result.getMessage()).contains("Password updated.");
        assertThat(result).isInstanceOf(MessageDto.class);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdatePassword_WhenWrongCurrentPassword_ThrowsIllegalStateException() {
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto();
        requestDto.setCurrentPassword("wrongPassword");
        requestDto.setNewPassword("newPassword");
        requestDto.setConfirmationPassword("newPassword");

        User user = new User();
        user.setPassword(passwordEncoder.encode("correctPassword"));

        given(utilService.getCurrentUser()).willReturn(user);
        given(passwordEncoder.matches("wrongPassword", user.getPassword())).willReturn(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> userService.updatePassword(requestDto));

        assertThat(exception.getMessage()).contains("Wrong password.");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdatePassword_WhenPasswordsDoNotMatch_ThrowsIllegalStateException() {
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto();
        requestDto.setCurrentPassword("oldPassword");
        requestDto.setNewPassword("newPassword");
        requestDto.setConfirmationPassword("newPassword123");

        User user = new User();
        user.setPassword(passwordEncoder.encode("oldPassword"));

        given(utilService.getCurrentUser()).willReturn(user);
        given(passwordEncoder.matches("oldPassword", user.getPassword())).willReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                userService.updatePassword(requestDto));

        assertThat(exception.getMessage()).contains("Passwords don't match.");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser_WithUsernameChange_UpdatesUsername() throws IOException {
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto();
        requestDto.setUsername("newUsername");

        User mockUser = new User();
        mockUser.setUsername("oldUsername");

        given(utilService.getCurrentUser()).willReturn(mockUser);

        UpdateUserResponseDto result = userService.updateUser(requestDto, null);

        verify(userRepository).save(any(User.class));
        assertEquals("newUsername", result.getUsername());
    }

    @Test
    void testUpdateUser_WithNullUsername_DoesNotUpdateUsername() throws IOException {
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto();
        requestDto.setUsername(null);

        User mockUser = new User();
        mockUser.setUsername("oldUsername");

        given(utilService.getCurrentUser()).willReturn(mockUser);

        UpdateUserResponseDto result = userService.updateUser(requestDto, null);

        verify(userRepository).save(any(User.class));
        assertEquals(mockUser.getUsername(),result.getUsername() );
    }

    @Test
    void testUpdateUser_WithUsernameAndImageUpload_UpdatesUsernameAndImageUrl() throws IOException {
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto();
        requestDto.setUsername("newUsername");
        MockMultipartFile imageFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "imageContent".getBytes());

        User mockUser = new User();
        mockUser.setUsername("oldUsername");

        given(utilService.getCurrentUser()).willReturn(mockUser);
        given(fileUploadService.uploadUserImageFile(imageFile)).willReturn("newImageUrl");

        UpdateUserResponseDto result = userService.updateUser(requestDto, imageFile);

        verify(userRepository).save(any(User.class));
        assertEquals(requestDto.getUsername(),result.getUsername());
        assertEquals("newImageUrl", result.getImageUrl());
    }
}
