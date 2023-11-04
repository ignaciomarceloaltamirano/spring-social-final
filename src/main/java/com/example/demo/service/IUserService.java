package com.example.demo.service;

import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.UserProfileResponseDto;
import com.example.demo.dto.response.UserResponseDto;

import java.util.List;


public interface IUserService {
    List<UserProfileResponseDto> getUsers();
    UserProfileResponseDto getUser(String username);
}
