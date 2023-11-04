package com.example.demo.controller;

import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.UserProfileResponseDto;
import com.example.demo.dto.response.UserResponseDto;
import com.example.demo.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
public class UserController {
    private final IUserService userService;

    @GetMapping
    public ResponseEntity<List<UserProfileResponseDto>> getUsers(
    ) {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponseDto>getUser(
            @PathVariable("username") String username
    ){
      return ResponseEntity.ok(userService.getUser(username));
    }
}
