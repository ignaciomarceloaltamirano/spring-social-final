package com.example.demo.service;

import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.UserResponseDto;


public interface IUserService {
    PageDto<UserResponseDto> getUsers(int page);
}
