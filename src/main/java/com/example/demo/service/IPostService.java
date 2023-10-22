package com.example.demo.service;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.PostRequestDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.PostResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IPostService {
    PageDto<PostResponseDto> getPosts(int page);
    PostResponseDto getPost(Long postId);
    PageDto<PostResponseDto> getPostsByTag(String tagName, int page);
    PageDto<PostResponseDto> getUserUpVotedPosts(int page);
    PageDto<PostResponseDto> getUserSubscribedCommunitiesPosts(int page);
    PageDto<PostResponseDto> getUserDownVotedPosts(int page);
    PageDto<PostResponseDto> getPostsByCommunity(Long communityId,int page);
    PageDto<PostResponseDto> getUserPosts(Long userId,int page);
    PageDto<PostResponseDto> getUserSavedPosts(int page);
    boolean isPostSaved(Long postId);
    MessageDto savePost(Long postId);
    MessageDto unSavePost(Long postId);
    PostResponseDto createPost(PostRequestDto postRequestDto, Long communityId, MultipartFile file) throws IOException;
    MessageDto deletePost(Long postId);
}
