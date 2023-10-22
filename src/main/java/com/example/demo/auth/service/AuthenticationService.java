package com.example.demo.auth.service;

import com.example.demo.auth.dto.request.UserLoginRequestDto;
import com.example.demo.auth.dto.request.UserRegisterRequestDto;
import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.response.UserResponseDto;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.enumeration.ERole;
import com.example.demo.exception.TokenRefreshException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IFileUploadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final IFileUploadService fileUploadService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final ModelMapper modelMapper;

    public MessageDto register(UserRegisterRequestDto userRegisterRequestDto, MultipartFile file) throws IOException {
        validateUser(userRegisterRequestDto);
        User user = createUser(userRegisterRequestDto, file);
        Set<Role> roles = assignUserRoles(user);
        user.setRoles(roles);
        userRepository.save(user);
        return new MessageDto("Successfully registered!");
    }

    public MessageDto registerMod(UserRegisterRequestDto userRegisterRequestDto, MultipartFile file) throws IOException {
        validateUser(userRegisterRequestDto);
        User user = createUser(userRegisterRequestDto, file);
        Set<Role> roles = assignUserRoles(user, "mod");
        user.setRoles(roles);
        userRepository.save(user);
        return new MessageDto("Successfully registered!");
    }

    public MessageDto registerAdmin(UserRegisterRequestDto userRegisterRequestDto, MultipartFile file) throws IOException {
        validateUser(userRegisterRequestDto);
        User user = createUser(userRegisterRequestDto, file);
        Set<Role> roles = assignUserRoles(user, "mod","admin");
        user.setRoles(roles);
        userRepository.save(user);
        return new MessageDto("Successfully registered!");
    }

    public ResponseEntity<UserResponseDto> login(UserLoginRequestDto userLoginRequestDto) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginRequestDto.getUsername(),
                        userLoginRequestDto.getPassword()
                )
        );
        context.setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtService.generateJwtCookie(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        ResponseCookie jwtRefreshCookie = jwtService.generateJwtRefreshCookie(refreshToken.getToken());

        User user = userRepository.findById(userDetails.getId()).get();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(modelMapper.map(user, UserResponseDto.class));
    }

    public ResponseEntity<MessageDto> logout() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Objects.equals(principal.toString(), "anonymousUser")) {
            Long userId = ((UserDetailsImpl) principal).getId();
            refreshTokenService.deleteByUserId(userId);
        }

        ResponseCookie jwtCookie = jwtService.getCleanJwtCookie();
        ResponseCookie jwtRefreshCookie = jwtService.getCleanJwtRefreshCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(new MessageDto("You've been logged out"));
    }

    public ResponseEntity<MessageDto> refreshToken(HttpServletRequest request) {
        String token = jwtService.getJwtRefreshFromCookie(request);
        if (token != null && !token.isEmpty()) {
            return refreshTokenService.findByToken(token)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ResponseCookie jwtCookie = jwtService.generateJwtCookie(user);
                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                .body(new MessageDto("Refresh Token successfully refreshed"));
                    })
                    .orElseThrow(() -> new TokenRefreshException(token, "Refresh Token is not in DB"));
        }
        return ResponseEntity.badRequest().body(new MessageDto("Refresh Token is empty"));
    }

    private Set<Role> assignUserRoles(User user, String... strRoles) {
        Set<Role> roles = new HashSet<>();
        roles.add(createRoleIfNotExists(ERole.ROLE_USER));
        Arrays.asList(strRoles).forEach(role -> {
            switch (role) {
                case "admin":
                    roles.add(createRoleIfNotExists(ERole.ROLE_ADMIN));
                    break;
                case "mod":
                    roles.add(createRoleIfNotExists(ERole.ROLE_MOD));
                    break;
                default:
                    break;
            }
        });
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
