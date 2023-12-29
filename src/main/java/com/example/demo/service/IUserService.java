package com.example.demo.service;

import com.example.demo.dto.request.UpdatePasswordRequestDto;
import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.UpdateUserRequestDto;
import com.example.demo.dto.response.UpdateUserResponseDto;
import com.example.demo.dto.response.UserProfileResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface IUserService {
    List<UserProfileResponseDto> getUsers();
    UserProfileResponseDto getUser(String username);
    MessageDto updatePassword(UpdatePasswordRequestDto updatePasswordRequestDto);
    UpdateUserResponseDto updateUser(UpdateUserRequestDto updateUserRequestDto, MultipartFile file) throws IOException;
}
