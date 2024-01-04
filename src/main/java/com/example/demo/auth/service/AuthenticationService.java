package com.example.demo.auth.service;

import com.example.demo.auth.dto.request.RefreshTokenRequestDto;
import com.example.demo.auth.dto.request.UserLoginRequestDto;
import com.example.demo.auth.dto.request.UserRegisterRequestDto;
import com.example.demo.auth.dto.response.LoginResponseDto;
import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.auth.dto.response.TokenRefreshResponseDto;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.Role;
import com.example.demo.entity.Token;
import com.example.demo.entity.User;
import com.example.demo.enumeration.ERole;
import com.example.demo.exception.TokenRefreshException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.TokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IFileUploadService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final IFileUploadService fileUploadService;

    public MessageDto register(UserRegisterRequestDto userRegisterRequestDto, MultipartFile file) throws IOException {
        validateUser(userRegisterRequestDto);
        var user = createUser(userRegisterRequestDto, file);
        Set<Role> roles = assignUserRoles(user);
        user.setRoles(roles);
        userRepository.save(user);
        return new MessageDto("Successfully registered!");
    }

    public LoginResponseDto login(UserLoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        var jwt = jwtService.generateToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);
        revokeAllUserTokens(user);
        saveUserToken(user, jwt);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        return LoginResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .roles(roles)
                .imageUrl(user.getImageUrl())
                .accessToken(jwt)
                .refreshToken(refreshToken)
                .build();
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public TokenRefreshResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            var user = userRepository.findByEmail(userEmail).orElseThrow();
            UserDetails userDetails = UserDetailsImpl.build(user);
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                var accessToken = jwtService.generateToken(userDetails);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                System.out.println("Token refreshed");
                return TokenRefreshResponseDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
        throw new TokenRefreshException(refreshToken, "Refresh token is not in database");
    }


    private Set<Role> assignUserRoles(User user) {
        Set<Role> roles = new HashSet<>();
        roles.add(createRoleIfNotExists(ERole.ROLE_USER));
        return roles;
    }

    private Role createRoleIfNotExists(ERole name) {
        Optional<Role> existingRole = roleRepository.findByName(name);
        if (existingRole.isPresent()) {
            return existingRole.get();
        } else {
            Role newRole = new Role();
            newRole.setName(name);
            return roleRepository.save(newRole);
        }
    }

    private User createUser(UserRegisterRequestDto userRegisterRequestDto, MultipartFile file) throws IOException {
        User user = User.builder()
                .username(userRegisterRequestDto.getUsername())
                .email(userRegisterRequestDto.getEmail())
                .password(passwordEncoder.encode(userRegisterRequestDto.getPassword()))
                .build();

        if (file != null) {
            String imageUrl = fileUploadService.uploadUserImageFile(file);
            user.setImageUrl(imageUrl);
        }
        return user;
    }

    private void validateUser(UserRegisterRequestDto userRegisterRequestDto) {
        if (userRepository.existsByUsername(userRegisterRequestDto.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists.");
        }

        if (userRepository.existsByEmail(userRegisterRequestDto.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use.");
        }
    }
}
