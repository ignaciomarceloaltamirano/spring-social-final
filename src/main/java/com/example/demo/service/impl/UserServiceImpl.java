package com.example.demo.service.impl;

import com.example.demo.auth.service.UserDetailsImpl;
import com.example.demo.dto.request.UpdateUserRequestDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.PostResponseDto;
import com.example.demo.dto.response.UserResponseDto;
import com.example.demo.entity.Post;
import com.example.demo.entity.Tag;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IFileUploadService;
import com.example.demo.service.IUserService;
import com.example.demo.service.IUtilService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public PageDto<UserResponseDto> getUsers(int page) {
        Sort s = Sort.by("id").ascending();
        Page<User> pageRequest = userRepository.findAll(PageRequest.of(page - 1, 2, s));
        int totalPages = pageRequest.getTotalPages();
        List<UserResponseDto> content = pageRequest.getContent().stream().map(
                user -> modelMapper.map(user, UserResponseDto.class)).toList();
        return new PageDto<>(content, totalPages);
    }
}
