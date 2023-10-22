package com.example.demo.service.impl;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.PostRequestDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.PostResponseDto;
import com.example.demo.entity.Community;
import com.example.demo.entity.Post;
import com.example.demo.entity.Tag;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import com.example.demo.service.IFileUploadService;
import com.example.demo.service.IPostService;
import com.example.demo.service.IUtilService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements IPostService {
    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final IUtilService utilService;
    private final IFileUploadService fileUploadService;
    private final ModelMapper modelMapper;

    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        return modelMapper.map(post, PostResponseDto.class);
    }

    public PageDto<PostResponseDto> getPosts(int page) {
        Sort s = Sort.by("id").ascending();
        Page<Post> pageRequest = postRepository.findAll(PageRequest.of(page - 1, 2, s));
        int totalPages = pageRequest.getTotalPages();
        List<PostResponseDto> content = pageRequest.getContent()
                .stream().map(
                        post -> modelMapper.map(post, PostResponseDto.class)).toList();
        return new PageDto<>(content, totalPages);
    }

    public PageDto<PostResponseDto> getPostsByTag(String tagName, int page) {
        Sort s = Sort.by("id").ascending();

        Page<Post>pageRequest=postRepository.findAllByTagsName(tagName, PageRequest.of(page - 1, 2, s));

        int totalPages = pageRequest.getTotalPages();
        List<PostResponseDto> content = pageRequest
                .getContent().stream().map(
                        post -> modelMapper.map(post, PostResponseDto.class)).toList();
        return new PageDto<>(content, totalPages);
    }

    public PageDto<PostResponseDto> getUserUpVotedPosts(int page) {
        User user = utilService.getCurrentUser();
        Sort s = Sort.by("id").ascending();
        Page<Post> pageRequest=voteRepository.findUserUpVotedPosts(user.getId(), PageRequest.of(page - 1, 2, s));

        int totalPages = pageRequest.getTotalPages();
        List<PostResponseDto> content = pageRequest
                .getContent().stream().map(
                        post -> modelMapper.map(post, PostResponseDto.class)).toList();
        return new PageDto<>(content, totalPages);
    }
    public PageDto<PostResponseDto> getUserDownVotedPosts(int page) {
        User user = utilService.getCurrentUser();
        Sort s = Sort.by("id").ascending();

        Page<Post> pageRequest=voteRepository.findUserDownVotedPosts(user.getId(), PageRequest.of(page - 1, 2, s));
        int totalPages = pageRequest.getTotalPages();
        List<PostResponseDto> content = pageRequest
                .getContent().stream().map(
                        post -> modelMapper.map(post, PostResponseDto.class)).toList();
        return new PageDto<>(content, totalPages);
    }

    public PageDto<PostResponseDto> getUserSubscribedCommunitiesPosts(int page) {
        User user = utilService.getCurrentUser();
        Sort s = Sort.by("id").ascending();

        Page<Post>pageRequest=postRepository.findPostsInUserSubscribedCommunities(user.getId(), PageRequest.of(page - 1, 2, s));

        int totalPages = pageRequest.getTotalPages();
        List<PostResponseDto> content = pageRequest
                .getContent().stream().map(
                        post -> modelMapper.map(post, PostResponseDto.class)).toList();
        return new PageDto<>(content, totalPages);
    }


    public PageDto<PostResponseDto> getPostsByCommunity(Long communityId, int page) {
        Sort s = Sort.by("id").ascending();

        Page<Post>pageRequest=postRepository.findAllByCommunityId(communityId, PageRequest.of(page - 1, 2, s));
        int totalPages = pageRequest.getTotalPages();
        List<PostResponseDto> content = pageRequest
                .getContent().stream().map(
                        post -> modelMapper.map(post, PostResponseDto.class)).toList();
        return new PageDto<>(content, totalPages);
    }

    public PageDto<PostResponseDto> getUserPosts(Long userId, int page) {
        Sort s = Sort.by("id").ascending();

        Page<Post> pageRequest=postRepository.findAllByAuthorId(userId, PageRequest.of(page - 1, 2, s));

        int totalPages = pageRequest.getTotalPages();
        List<PostResponseDto> content = pageRequest
                .getContent().stream().map(
                        post -> modelMapper.map(post, PostResponseDto.class)).toList();
        return new PageDto<>(content, totalPages);
    }

    public PageDto<PostResponseDto> getUserSavedPosts(int page) {
        Sort s = Sort.by("id").ascending();
        User user = utilService.getCurrentUser();

        Page<Post> pageRequest=userRepository.findSavedPostsById(user.getId(), PageRequest.of(page - 1, 2, s));

        int totalPages = pageRequest.getTotalPages();
        List<PostResponseDto> content = pageRequest
                .getContent().stream().map(
                        post -> modelMapper.map(post, PostResponseDto.class)).toList();
        return new PageDto<>(content, totalPages);
    }

    public boolean isPostSaved(Long postId) {
        User user = utilService.getCurrentUser();
        return userRepository.isPostSavedByUser(user.getId(), postId);
    }

    public MessageDto savePost(Long postId) {
        User user = utilService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        user.savePost(post);
        userRepository.save(user);
        return new MessageDto("Post saved");
    }

    public MessageDto unSavePost(Long postId) {
        User user = utilService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        user.unSavePost(post);
        userRepository.save(user);
        return new MessageDto("Post unsaved");
    }

    public PostResponseDto createPost(PostRequestDto postRequestDto, Long communityId, MultipartFile file) throws IOException {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));
        Set<Tag> tags = new HashSet<>();
        List<Tag> newTags = new ArrayList<>();
        Post newPost = Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .author(utilService.getCurrentUser())
                .community(community)
                .build();

        if (postRequestDto.getTags() != null && !postRequestDto.getTags().isEmpty()) {
            for (String tagName : postRequestDto.getTags()) {
                Optional<Tag> existingTag = tagRepository.findByName(tagName);
                if (existingTag.isPresent()) {
                    tags.add(existingTag.get());
                } else {
                    Tag newTag = Tag.builder()
                            .name(tagName)
                            .build();
                    newTags.add(newTag);
                }
            }

            if (!newTags.isEmpty()) {
                newTags = tagRepository.saveAll(newTags);
                tags.addAll(newTags);
            }

            newPost.setTags(tags);
        }

        if (file != null) {
            String imageUrl = fileUploadService.uploadFile(file);
            newPost.setImageUrl(imageUrl);
        }
        postRepository.save(newPost);
        return modelMapper.map(newPost, PostResponseDto.class);
    }

    public MessageDto deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        postRepository.delete(post);
        return new MessageDto("Post deleted");
    }
}
