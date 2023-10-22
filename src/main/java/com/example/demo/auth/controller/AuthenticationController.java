package com.example.demo.auth.controller;

import com.example.demo.auth.dto.request.UserLoginRequestDto;
import com.example.demo.auth.dto.request.UserRegisterRequestDto;
import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.auth.service.AuthenticationService;
import com.example.demo.dto.response.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping(value = "/register",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> registerUser(
            @RequestPart("user") UserRegisterRequestDto userRegisterRequestDto,
            @RequestPart(value = "image",required = false)MultipartFile file
            ) throws IOException {
        return ResponseEntity.ok(authenticationService.register(userRegisterRequestDto,file));
    }

    @PostMapping(value = "/register-mod",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> registerMod(
            @RequestPart("user") UserRegisterRequestDto userRegisterRequestDto,
            @RequestPart(value = "image",required = false)MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(authenticationService.registerMod(userRegisterRequestDto,file));
    }

    @PostMapping(value = "/register-admin",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> registerAdmin(
            @RequestPart("user") UserRegisterRequestDto userRegisterRequestDto,
            @RequestPart(value = "image",required = false)MultipartFile file
            ) throws IOException {
        return ResponseEntity.ok(authenticationService.registerAdmin(userRegisterRequestDto,file));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto>login(
            @RequestBody UserLoginRequestDto userLoginRequestDto
            ){
        return authenticationService.login(userLoginRequestDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageDto>logout(    ){
        return authenticationService.logout();
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<MessageDto>refreshToken(HttpServletRequest request){
        return authenticationService.refreshToken(request);
    }
}
