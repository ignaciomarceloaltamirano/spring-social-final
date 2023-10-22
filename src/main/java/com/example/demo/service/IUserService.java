package com.example.demo.service;

import com.example.demo.dto.request.UpdateUserRequestDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IUserService {
    PageDto<UserResponseDto> getUsers(int page);
}
