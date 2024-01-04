package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileUploadService {
    String uploadPostFile(MultipartFile multipartFile) throws IOException;
    String uploadUserImageFile(MultipartFile multipartFile) throws IOException;
}
