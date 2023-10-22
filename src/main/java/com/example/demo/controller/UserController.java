package com.example.demo.controller;

import com.example.demo.dto.request.UpdateUserRequestDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.UserResponseDto;
import com.example.demo.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @GetMapping("/page/{page}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PageDto<UserResponseDto>> getUsers(
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(userService.getUsers(page));
    }
}
