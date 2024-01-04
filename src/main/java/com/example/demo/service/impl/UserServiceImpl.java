package com.example.demo.service.impl;

import com.example.demo.auth.service.UserDetailsImpl;
import com.example.demo.dto.request.UpdatePasswordRequestDto;
import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.UpdateUserRequestDto;
import com.example.demo.dto.response.UpdateUserResponseDto;
import com.example.demo.dto.response.UserProfileResponseDto;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IFileUploadService;
import com.example.demo.service.IUserService;
import com.example.demo.service.IUtilService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final IUtilService utilService;
    private final IFileUploadService fileUploadService;
    private final PasswordEncoder passwordEncoder;

    public List<UserProfileResponseDto> getUsers() {
        List<User> users = userRepository.findAll();

        return users.stream().map(user -> modelMapper.map(user, UserProfileResponseDto.class)).toList();
    }

    @Override
    public UserProfileResponseDto getUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return modelMapper.map(user, UserProfileResponseDto.class);
    }

    @Override
    public MessageDto updatePassword(UpdatePasswordRequestDto request) {
        var user = utilService.getCurrentUser();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password.");
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Passwords don't match.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return new MessageDto("Password updated.");
    }

    @Override
    public UpdateUserResponseDto updateUser(UpdateUserRequestDto updateUserRequestDto, MultipartFile file) throws IOException {
        var user = utilService.getCurrentUser();

        if (updateUserRequestDto.getUsername() != null &&
                !Objects.equals(user.getUsername(), updateUserRequestDto.getUsername())) {
            user.setUsername(updateUserRequestDto.getUsername());
        }
        if (updateUserRequestDto.getEmail() != null &&
                !Objects.equals(user.getEmail(), updateUserRequestDto.getEmail())) {
            user.setEmail(updateUserRequestDto.getEmail());
        }

        if (file != null) {
            String imageUrl = fileUploadService.uploadUserImageFile(file);
            user.setImageUrl(imageUrl);
        }

        userRepository.save(user);
        return new UpdateUserResponseDto(user.getUsername(), user.getEmail(), user.getImageUrl());
    }
}
