package com.example.demo.auth.service;

import com.example.demo.auth.dto.request.RefreshTokenRequestDto;
import com.example.demo.auth.dto.request.UserLoginRequestDto;
import com.example.demo.auth.dto.request.UserRegisterRequestDto;
import com.example.demo.auth.dto.response.LoginResponseDto;
import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.auth.dto.response.TokenRefreshResponseDto;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.enumeration.ERole;
import com.example.demo.exception.TokenRefreshException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IFileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final IFileUploadService fileUploadService;

    public MessageDto register(UserRegisterRequestDto userRegisterRequestDto, MultipartFile file) throws IOException {
        validateUser(userRegisterRequestDto);
        User user = createUser(userRegisterRequestDto, file);
        Set<Role> roles = assignUserRoles(user);
        user.setRoles(roles);
        userRepository.save(user);
        return new MessageDto("Successfully registered!");
    }

    public LoginResponseDto login(UserLoginRequestDto userLoginRequestDto) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginRequestDto.getUsername(),
                        userLoginRequestDto.getPassword()
                )
        );
        context.setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwtToken = jwtService.generateJwtToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        User user = userRepository.findById(userDetails.getId()).get();

        return LoginResponseDto.builder()
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .roles(roles)
                .username(userDetails.getUsername())
                .imageUrl(user.getImageUrl())
                .refreshToken(refreshToken.getToken())
                .accessToken(jwtToken)
                .build();
    }

    public ResponseEntity<MessageDto> logout() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("PRINCIPAL FROM LOGOUT: " + principal.toString());
        if (!Objects.equals(principal.toString(), "anonymousUser")) {
            Long userId = ((UserDetailsImpl) principal).getId();
            refreshTokenService.deleteByUser(userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new MessageDto("You've been logged out"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageDto("Log out failed"));
    }

    public ResponseEntity<TokenRefreshResponseDto> refreshToken(RefreshTokenRequestDto request) {
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
//                    String token = jwtService.generateJwtFromUsername(user.getUsername());
                    String token = jwtService.generateJwtFromUserId(user.getId());
                    return ResponseEntity.ok(TokenRefreshResponseDto.builder()
                            .accessToken(token).refreshToken(requestRefreshToken).build());
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database. Please make a new log in."));
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
            String imageUrl = fileUploadService.uploadFile(file);
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
