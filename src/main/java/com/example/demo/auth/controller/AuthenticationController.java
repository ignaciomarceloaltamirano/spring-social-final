package com.example.demo.auth.controller;

import com.example.demo.auth.dto.request.RefreshTokenRequestDto;
import com.example.demo.auth.dto.request.UserLoginRequestDto;
import com.example.demo.auth.dto.request.UserRegisterRequestDto;
import com.example.demo.auth.dto.response.LoginResponseDto;
import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.auth.dto.response.TokenRefreshResponseDto;
import com.example.demo.auth.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> registerUser(
            @RequestPart("user") @Valid UserRegisterRequestDto userRegisterRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(authenticationService.register(userRegisterRequestDto, file));
    }

    @PostMapping(value = "/register-mod", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageDto> registerMod(
            @RequestPart("user") @Valid UserRegisterRequestDto userRegisterRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(authenticationService.registerMod(userRegisterRequestDto, file));
    }

    @PostMapping(value = "/register-admin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageDto> registerAdmin(
            @RequestPart("user") @Valid UserRegisterRequestDto userRegisterRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(authenticationService.registerAdmin(userRegisterRequestDto, file));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody @Valid UserLoginRequestDto userLoginRequestDto
    ) {
        return ResponseEntity.ok(authenticationService.login(userLoginRequestDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageDto> logout() {
        return authenticationService.logout();
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<TokenRefreshResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequestDto request) throws IOException {
        return authenticationService.refreshToken(request);
    }
}
