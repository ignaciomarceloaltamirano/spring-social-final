package com.example.demo.service.impl;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.UpdatePostRequestDto;
import com.example.demo.dto.request.PostRequestDto;
import com.example.demo.dto.response.DeletePostResponseDto;
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
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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
        Sort s = Sort.by("id").descending();
        Page<Post> pageRequest = postRepository.findAll(PageRequest.of(page - 1, 6, s));
        int totalPages = pageRequest.getTotalPages();
        int currentPage = pageRequest.getNumber() + 1;

        List<PostResponseDto> content = pageRequest.getContent()
                .stream().map(
                        post -> modelMapper.map(post, PostResponseDto.class)).toList();
        return new PageDto<>(content, totalPages, currentPage);
    }

    public PageDto<PostResponseDto> getPostsByTag(String tagName, int page) {
        Sort s = Sort.by("id").descending();

        Page<Post> pageRequest = postRepository.findAllByTagsName(tagName, PageRequest.of(page - 1, 6, s));
        int totalPages = pageRequest.getTotalPages();
        int currentPage = pageRequest.getNumber() + 1;

        List<PostResponseDto> content = pageRequest
                .getContent().stream().map(
                        post -> modelMapper.map(post, PostResponseDto.class)).toList();
        return new PageDto<>(content, totalPages, currentPage);
    }

    public PageDto<PostResponseDto> getUserUpVotedPosts(Long userId, int page) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Sort s = Sort.by("id").descending();
        Page<Post> pageRequest = voteRepository.findUserUpVotedPosts(user.getId(), PageRequest.of(page - 1, 6, s));
        int totalPages = pageRequest.getTotalPages();
        int currentPage = pageRequest.getNumber() + 1;

        List<PostResponseDto> content = pageRequest
                .getContent().stream().map(
                        post -> modelMapper.map(post, PostResponseDto.class)).toList();
        return new PageDto<>(content, totalPages, currentPage);
    }

    public PageDto<PostResponseDto> getUserDownVotedPosts(Long userId, int page) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Sort s = Sort.by("id").descending();

        Page<Post> pageRequest = voteRepository.findUserDownVotedPosts(user.getId(), PageRequest.of(page - 1, 6, s));
        int totalPages = pageRequest.getTotalPages();
        int currentPage = pageRequest.getNumber() + 1;

        List<PostResponseDto> content = pageRequest
                .getContent().stream().map(
                        post -> modelMapper.map(post, PostResponseDto.class)).toList();
        return new PageDto<>(content, totalPages, currentPage);
    }

    public PageDto<PostResponseDto> getUserSubscribedCommunitiesPosts(int page) {
        User user = utilService.getCurrentUser();
        Sort s = Sort.by("id").descending();

        Page<Post> pageRequest = postRepository.findPostsInUserSubscribedCommunities(user.getId(), PageRequest.of(page - 1, 6, s));
        int totalPages = pageRequest.getTotalPages();
        int currentPage = pageRequest.getNumber() + 1;

        List<PostResponseDto> content = pageRequest
                .getContent().stream().map(
                        post -> modelMapper.map(post, PostResponseDto.class)).toList();
        return new PageDto<>(content, totalPages, currentPage);
    }


    public PageDto<PostResponseDto> getPostsByCommunity(Long communityId, int page) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found."));
        Sort s = Sort.by("id").descending();

        Page<Post> pageRequest = postRepository.findAllByCommunityId(community.getId(), PageRequest.of(page - 1, 6, s));
        int totalPages = pageRequest.getTotalPages();
        int currentPage = pageRequest.getNumber() + 1;

        List<PostResponseDto> content = pageRequest
                .getContent().stream().map(
                        post -> modelMapper.map(post, PostResponseDto.class)).toList();
        return new PageDto<>(content, totalPages, currentPage);
    }

    public PageDto<PostResponseDto> getUserPosts(Long userId, int page) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        Sort s = Sort.by("id").descending();

        Page<Post> pageRequest = postRepository.findAllByAuthorId(user.getId(), PageRequest.of(page - 1, 6, s));
        int totalPages = pageRequest.getTotalPages();
        int currentPage = pageRequest.getNumber() + 1;

        List<PostResponseDto> content = pageRequest
                .getContent().stream().map(
                        post -> modelMapper.map(post, PostResponseDto.class)).toList();
        return new PageDto<>(content, totalPages, currentPage);
    }

    public PageDto<PostResponseDto> getUserSavedPosts(int page) {
        Sort s = Sort.by("id").descending();
        User user = utilService.getCurrentUser();

        Page<Post> pageRequest = userRepository.findSavedPostsById(user.getId(), PageRequest.of(page - 1, 6, s));
        int totalPages = pageRequest.getTotalPages();
        int currentPage = pageRequest.getNumber() + 1;

        List<PostResponseDto> content = pageRequest
                .getContent().stream().map(
                        post -> modelMapper.map(post, PostResponseDto.class)).toList();
        return new PageDto<>(content, totalPages, currentPage);
    }

    public boolean isPostSaved(Long postId) {
        User user = utilService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found."));

        return userRepository.isPostSavedByUser(user.getId(), post.getId());
    }

    public MessageDto savePost(Long postId) {
        User user = utilService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found."));

        user.savePost(post);
        userRepository.save(user);
        return new MessageDto("Post saved.");
    }

    public MessageDto unSavePost(Long postId) {
        User user = utilService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found."));

        user.unSavePost(post);
        userRepository.save(user);
        return new MessageDto("Post unsaved.");
    }

    public PostResponseDto createPost(PostRequestDto postRequestDto, Long communityId, MultipartFile file) throws IOException {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found."));
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
                    if (!tagName.isEmpty()) {
                        Tag newTag = Tag.builder()
                                .name(tagName)
                                .build();
                        newTags.add(newTag);
                    }
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

    @Transactional
    public PostResponseDto updatePost(@Valid UpdatePostRequestDto postRequestDto, Long postId, MultipartFile file) throws IOException {
        Post postToUpdate = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found."));
        Set<Tag> tags = new HashSet<>();
        List<Tag> newTags = new ArrayList<>();

        if (postRequestDto.getTags() != null && !postRequestDto.getTags().isEmpty()) {
            for (String tagName : postRequestDto.getTags()) {
                Optional<Tag> existingTag = tagRepository.findByName(tagName);
                if (existingTag.isPresent()) {
                    tags.add(existingTag.get());
                } else {
                    if (!tagName.isEmpty()) {
                        Tag newTag = Tag.builder()
                                .name(tagName)
                                .build();
                        newTags.add(newTag);
                    }
                }
            }

            if (!newTags.isEmpty()) {
                newTags = tagRepository.saveAll(newTags);
                tags.addAll(newTags);
            }
            postToUpdate.setTags(tags);
        }

        if (file != null) {
            String imageUrl = fileUploadService.uploadFile(file);
            postToUpdate.setImageUrl(imageUrl);
        }

        if (!postRequestDto.getTitle().equals(postToUpdate.getTitle())) {
            postToUpdate.setTitle(postRequestDto.getTitle());
        }

        if (!postRequestDto.getContent().equals(postToUpdate.getContent())) {
            postToUpdate.setContent(postRequestDto.getContent());
        }
        return modelMapper.map(postToUpdate, PostResponseDto.class);
    }

    public DeletePostResponseDto deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found."));
        postRepository.delete(post);
        return new DeletePostResponseDto(post.getCommunity().getName());
    }
}
