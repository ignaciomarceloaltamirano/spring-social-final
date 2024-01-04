package com.example.demo.service;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.PostRequestDto;
import com.example.demo.dto.response.DeletePostResponseDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.PostResponseDto;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import com.example.demo.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTests {
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommunityRepository communityRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private VoteRepository voteRepository;
    @Mock
    private IUtilService utilService;
    @Mock
    private IFileUploadService fileUploadService;
    @Spy
    private ModelMapper modelMapper;
    @InjectMocks
    private PostServiceImpl postService;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .username("author")
                .email("test@test.com")
                .password("test")
                .savedPosts(new HashSet<>())
                .build();
    }

    @Test
    void testGetPost() {
        given(postRepository.findById(anyLong())).willReturn(Optional.of(new Post()));

        PostResponseDto result = postService.getPost(anyLong());

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(PostResponseDto.class);
    }

    @Test
    void testGetPost_WhenPostNotFound_ThrowsResourceNotFoundException() {
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                postService.getPost(anyLong()));

        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetAllPosts() {
        Page<Post> page = new PageImpl<>(Collections.singletonList(new Post()));
        given(postRepository.findAll(any(PageRequest.class))).willReturn(page);

        PageDto<PostResponseDto> result = postService.getPosts(1);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(PageDto.class);
        verify(postRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void testGetPostsByTag() {
        Page<Post> page = new PageImpl<>(Collections.singletonList(new Post()));
        given(postRepository.findAllByTagsName(eq("test"), any(PageRequest.class))).willReturn(page);

        PageDto<PostResponseDto> result = postService.getPostsByTag("test", 1);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(PageDto.class);
        verify(postRepository, times(1)).findAllByTagsName(eq("test"), any(PageRequest.class));
    }

//    @Test
//    void testGetUserUpVotedPosts() {
//        given(utilService.getCurrentUser()).willReturn(user);
//        Page<Post> page = new PageImpl<>(Collections.singletonList(new Post()));
//        given(voteRepository.findUserUpVotedPosts(anyLong(), any(PageRequest.class))).willReturn(page);
//
//        PageDto<PostResponseDto> result = postService.getUserUpVotedPosts(userId, 1);
//
//        assertThat(result).isNotNull();
//        assertThat(result).isInstanceOf(PageDto.class);
//        verify(voteRepository, times(1)).findUserUpVotedPosts(anyLong(), any(PageRequest.class));
//    }

//    @Test
//    void testGetUserDownVotedPosts() {
//        given(utilService.getCurrentUser()).willReturn(user);
//        Page<Post> page = new PageImpl<>(Collections.singletonList(new Post()));
//        given(voteRepository.findUserDownVotedPosts(anyLong(), any(PageRequest.class))).willReturn(page);
//
//        PageDto<PostResponseDto> result = postService.getUserDownVotedPosts(userId, 1);
//
//        assertThat(result).isNotNull();
//        assertThat(result).isInstanceOf(PageDto.class);
//        verify(voteRepository, times(1)).findUserDownVotedPosts(anyLong(), any(PageRequest.class));
//    }

    @Test
    void testGetUserSubscribedCommunitiesPosts() {
        given(utilService.getCurrentUser()).willReturn(user);
        Page<Post> page = new PageImpl<>(Collections.singletonList(new Post()));
        given(postRepository.findPostsInUserSubscribedCommunities(anyLong(), any(PageRequest.class))).willReturn(page);

        PageDto<PostResponseDto> result = postService.getUserSubscribedCommunitiesPosts(1);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(PageDto.class);
        verify(postRepository, times(1)).findPostsInUserSubscribedCommunities(anyLong(), any(PageRequest.class));
    }

    @Test
    void testGetPostsByCommunity() {
        Page<Post> page = new PageImpl<>(Collections.singletonList(new Post()));
        given(postRepository.findAllByCommunityId(anyLong(), any(PageRequest.class))).willReturn(page);

        PageDto<PostResponseDto> result = postService.getPostsByCommunity(1L, 1);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(PageDto.class);
        verify(postRepository, times(1)).findAllByCommunityId(anyLong(), any(PageRequest.class));
    }

    @Test
    void testGetUserPosts() {
        Page<Post> page = new PageImpl<>(Collections.singletonList(new Post()));
        given(postRepository.findAllByAuthorId(anyLong(), any(PageRequest.class))).willReturn(page);

        PageDto<PostResponseDto> result = postService.getUserPosts(1L, 1);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(PageDto.class);
        verify(postRepository, times(1)).findAllByAuthorId(anyLong(), any(PageRequest.class));
    }

    @Test
    void testGetUserSavedPosts() {
        given(utilService.getCurrentUser()).willReturn(user);
        Page<Post> page = new PageImpl<>(Collections.singletonList(new Post()));
        given(userRepository.findSavedPostsById(anyLong(), any(PageRequest.class))).willReturn(page);

        PageDto<PostResponseDto> result = postService.getUserSavedPosts(1);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(PageDto.class);
        verify(userRepository, times(1)).findSavedPostsById(anyLong(), any(PageRequest.class));
    }

    @Test
    void testIsPostSaved_WhenItIsSaved_ReturnsTrue() {
        given(utilService.getCurrentUser()).willReturn(user);
        given(userRepository.isPostSavedByUser(anyLong(), anyLong())).willReturn(true);

        boolean result = postService.isPostSaved(1L);

        assertTrue(result);
        verify(userRepository, times(1)).isPostSavedByUser(anyLong(), anyLong());
    }

    @Test
    void testIsPostSaved_WhenItIsNotSaved_ReturnsFalse() {
        given(utilService.getCurrentUser()).willReturn(user);
        given(userRepository.isPostSavedByUser(anyLong(), anyLong())).willReturn(false);

        boolean result = postService.isPostSaved(1L);

        assertFalse(result);
        verify(userRepository, times(1)).isPostSavedByUser(anyLong(), anyLong());
    }

    @Test
    void testSavePost_Success_ReturnsMessageDto() {
        given(utilService.getCurrentUser()).willReturn(user);
        given(postRepository.findById(anyLong())).willReturn(Optional.of(new Post()));

        MessageDto result = postService.savePost(1L);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(MessageDto.class);
    }

    @Test
    void testSavePost_WhenPostNotFound_ThrowsResourceNotFoundException() {
        given(utilService.getCurrentUser()).willReturn(user);
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                postService.savePost(1L));

        verify(postRepository, times(1)).findById(anyLong());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUnSavePost_Success_ReturnsMessageDto() {
        given(utilService.getCurrentUser()).willReturn(user);
        given(postRepository.findById(anyLong())).willReturn(Optional.of(new Post()));

        MessageDto result = postService.unSavePost(1L);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(MessageDto.class);
    }

    @Test
    void testUnSavePost_WhenPostNotFound_ThrowsResourceNotFoundException() {
        given(utilService.getCurrentUser()).willReturn(user);
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                postService.unSavePost(1L));

        verify(postRepository, times(1)).findById(anyLong());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreatePost_WithFile_ReturnsPost() throws IOException {
        given(communityRepository.findById(anyLong())).willReturn(Optional.of(new Community()));

        PostRequestDto postRequestDto = PostRequestDto.builder()
                .title("title")
                .content("content")
                .build();

        MultipartFile file = mock(MultipartFile.class);

        given(communityRepository.findById(anyLong())).willReturn(Optional.of(new Community()));

        given(fileUploadService.uploadPostFile(file)).willReturn("testImageUrl");

        given(postRepository.save(any(Post.class))).willReturn(new Post());

        PostResponseDto result = postService.createPost(postRequestDto, anyLong(), file);

        assertThat(result).isNotNull();
        assertThat(result.getImageUrl()).isNotNull();
        assertThat(result).isInstanceOf(PostResponseDto.class);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testCreatePost_WithoutFile_ReturnsPost() throws IOException {
        given(communityRepository.findById(anyLong())).willReturn(Optional.of(new Community()));

        PostRequestDto postRequestDto = PostRequestDto.builder()
                .title("title")
                .content("content")
                .build();

        given(communityRepository.findById(anyLong())).willReturn(Optional.of(new Community()));

        given(postRepository.save(any(Post.class))).willReturn(new Post());

        PostResponseDto result = postService.createPost(postRequestDto, anyLong(), null);

        assertThat(result).isNotNull();
        assertThat(result.getImageUrl()).isNull();
        assertThat(result).isInstanceOf(PostResponseDto.class);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testCreatePost_WithFileAndWithoutTags_ReturnsPost() throws IOException {
        given(communityRepository.findById(anyLong())).willReturn(Optional.of(new Community()));

        PostRequestDto postRequestDto = PostRequestDto.builder()
                .title("title")
                .content("content")
                .build();

        MultipartFile file = mock(MultipartFile.class);

        given(communityRepository.findById(anyLong())).willReturn(Optional.of(new Community()));

        given(fileUploadService.uploadPostFile(file)).willReturn("testImageUrl");

        given(postRepository.save(any(Post.class))).willReturn(new Post());

        PostResponseDto result = postService.createPost(postRequestDto, anyLong(), file);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(PostResponseDto.class);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testCreatePost_WithFileAndTags_ReturnsPost() throws IOException {
        given(communityRepository.findById(anyLong())).willReturn(Optional.of(new Community()));

        PostRequestDto postRequestDto = PostRequestDto.builder()
                .title("title")
                .content("content")
                .tags(Set.of("tag1", "tag2"))
                .build();

        MultipartFile file = mock(MultipartFile.class);

        given(communityRepository.findById(anyLong())).willReturn(Optional.of(new Community()));

        if (postRequestDto.getTags() != null) {
            for (String tagName : postRequestDto.getTags()) {
                given(tagRepository.findByName(tagName)).willReturn(Optional.of(new Tag()));
            }
        }

        given(fileUploadService.uploadPostFile(file)).willReturn("testImageUrl");

        given(postRepository.save(any(Post.class))).willReturn(new Post());

        PostResponseDto result = postService.createPost(postRequestDto, anyLong(), file);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(PostResponseDto.class);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testCreatePost_WithoutFileAndWithoutTags_ReturnsPost() throws IOException {
        given(communityRepository.findById(anyLong())).willReturn(Optional.of(new Community()));

        PostRequestDto postRequestDto = PostRequestDto.builder()
                .title("title")
                .content("content")
                .build();

        given(communityRepository.findById(anyLong())).willReturn(Optional.of(new Community()));

        given(postRepository.save(any(Post.class))).willReturn(new Post());

        PostResponseDto result = postService.createPost(postRequestDto, anyLong(), null);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(PostResponseDto.class);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testCreatePost_WithoutFileAndWithTags_ReturnsPost() throws IOException {
        given(communityRepository.findById(anyLong())).willReturn(Optional.of(new Community()));

        PostRequestDto postRequestDto = PostRequestDto.builder()
                .title("title")
                .content("content")
                .tags(Set.of("tag1", "tag2"))
                .build();

        given(communityRepository.findById(anyLong())).willReturn(Optional.of(new Community()));

        if (postRequestDto.getTags() != null) {
            for (String tagName : postRequestDto.getTags()) {
                given(tagRepository.findByName(tagName)).willReturn(Optional.of(new Tag()));
            }
        }

        given(postRepository.save(any(Post.class))).willReturn(new Post());

        PostResponseDto result = postService.createPost(postRequestDto, anyLong(), null);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(PostResponseDto.class);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testCreatePost_WhenCommunityNotFound_ThrowsResourceNotFoundException() {
        given(communityRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                postService.createPost(new PostRequestDto(), anyLong(), null));

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testDeletePost_Success() {
        given(postRepository.findById(anyLong())).willReturn(Optional.of(new Post()));

        DeletePostResponseDto result = postService.deletePost(anyLong());

        assertThat(result).isInstanceOf(MessageDto.class);
        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    void testDeletePost_WhenPostNotFound_ThrowsResourceNotFoundException() {
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->
                postService.deletePost(anyLong()));

        verify(postRepository, never()).delete(new Post());
    }
}
