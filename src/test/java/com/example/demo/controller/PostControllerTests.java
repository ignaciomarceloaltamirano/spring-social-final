package com.example.demo.controller;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.auth.service.JwtService;
import com.example.demo.auth.service.UserDetailsServiceImpl;
import com.example.demo.dto.request.UpdatePostRequestDto;
import com.example.demo.dto.request.PostRequestDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.PostResponseDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.TokenRepository;
import com.example.demo.service.IPostService;
import com.example.demo.service.IUtilService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@WithMockUser(username = "user", password = "test", roles = "USER")
@AutoConfigureMockMvc(addFilters = false)
public class PostControllerTests {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private IPostService postService;
    @MockBean
    private ModelMapper modelMapper;
    @MockBean
    private TokenRepository tokenRepository;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private IUtilService utilService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void testGetPost() throws Exception {
        given(postService.getPost(anyLong())).willReturn(PostResponseDto.builder().title("test title").build());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/posts/{postId}", 1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("test title"));
    }

    @Test
    public void testGetPost_WhenPostNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(postService.getPost(anyLong())).willThrow(new ResourceNotFoundException("Post not found"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/posts/{postId}", 1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetUserPosts() throws Exception {
        given(postService.getUserPosts(anyLong(), anyInt()))
                .willReturn(new PageDto<>(List.of(new PostResponseDto()), anyInt(), anyInt()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/posts/users/{userId}/page/{page}", 1L, 1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.currentPage").exists())
                .andExpect(jsonPath("$.totalPages").isNotEmpty());
    }

    @Test
    public void testGetUserPosts_WhenUserIsNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(postService.getUserPosts(anyLong(), anyInt()))
                .willThrow(new ResourceNotFoundException("User not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/posts/users/{userId}/page/{page}", 1L, 1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetUserSavedPosts() throws Exception {
        given(postService.getUserSavedPosts(anyInt()))
                .willReturn(new PageDto<>(List.of(new PostResponseDto()), 1, 1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/posts/saved/page/{page}", 1L, 1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.currentPage").exists())
                .andExpect(jsonPath("$.totalPages").isNotEmpty());
    }

    @Test
    public void testGetUserSubscribedCommunitiesPosts() throws Exception {
        given(postService.getUserSubscribedCommunitiesPosts(anyInt()))
                .willReturn(new PageDto<>(List.of(new PostResponseDto()), 1, 1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/posts/subscribed/page/{page}", 1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.currentPage").exists())
                .andExpect(jsonPath("$.totalPages").isNotEmpty());
    }

    @Test
    public void testGetUserUpVotedPosts() throws Exception {
        given(postService.getUserUpVotedPosts(anyLong(), anyInt()))
                .willReturn(new PageDto<>(List.of(new PostResponseDto()), 1, 1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/posts/upvoted/{userId}/page/{page}", 1L, 1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.currentPage").exists())
                .andExpect(jsonPath("$.totalPages").isNotEmpty());
    }

    @Test
    public void testGetUserUpVotedPosts_WhenUserNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(postService.getUserUpVotedPosts(anyLong(),anyInt()))
                .willThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/upvoted/{userId}/page/{page}", 1L,1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetUserDownVotedPosts() throws Exception {
        given(postService.getUserDownVotedPosts(anyLong(), anyInt()))
                .willReturn(new PageDto<>(List.of(new PostResponseDto()), 1, 1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/posts/downvoted/{userId}/page/{page}", 1, 1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.currentPage").exists())
                .andExpect(jsonPath("$.totalPages").isNotEmpty());
    }

    @Test
    public void testGetUserDownVotedPosts_WhenUserNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(postService.getUserDownVotedPosts(anyLong(),anyInt()))
                .willThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/downvoted/{userId}/page/{page}", 1L,1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPostsByTag() throws Exception {
        given(postService.getPostsByTag(anyString(), anyInt()))
                .willReturn(new PageDto<>(List.of(new PostResponseDto()), anyInt(), anyInt()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/posts/tag/{tagName}/page/{page}", "test", 1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.currentPage").exists())
                .andExpect(jsonPath("$.totalPages").isNotEmpty());
    }

    @Test
    public void testGetPostsByCommunity() throws Exception {
        given(postService.getPostsByCommunity(anyLong(), anyInt()))
                .willReturn(new PageDto<>(List.of(new PostResponseDto()), anyInt(), anyInt()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/posts/communities/{communityId}/page/{page}", 1L, 1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.currentPage").exists())
                .andExpect(jsonPath("$.totalPages").isNotEmpty());
    }

    @Test
    public void testGetPostsByCommunity_WhenCommunityIsNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(postService.getPostsByCommunity(anyLong(), anyInt()))
                .willThrow(new ResourceNotFoundException("Community not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/posts/communities/{communityId}/page/{page}", 1L, 1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testIsPostSaved_ReturnsTrue() throws Exception {
        given(postService.isPostSaved(anyLong()))
                .willReturn(true);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/posts/is-saved/{postId}", 1L)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testIsPostSaved_ReturnsFalse() throws Exception {
        given(postService.isPostSaved(anyLong()))
                .willReturn(false);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/posts/is-saved/{postId}", 1L)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testCreatePostWithFile_Success() throws Exception {
        PostRequestDto postRequestDto = PostRequestDto.builder()
                .title("test title")
                .content("test content")
                .tags(Set.of("tag1", "tag2", "tag3"))
                .build();

        String postDtoJson = objectMapper.writeValueAsString(postRequestDto);

        given(postService.createPost(any(PostRequestDto.class), anyLong(), any(MultipartFile.class)))
                .willReturn(new PostResponseDto());

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes());
        MockMultipartFile jsonFile = new MockMultipartFile("post", "", "application/json", postDtoJson.getBytes());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart("/posts/{communityId}", 1L)
                .file(imageFile)
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreatePostWithoutFile_Success() throws Exception {
        PostRequestDto postRequestDto = PostRequestDto.builder()
                .title("test title")
                .content("test content")
                .tags(Set.of("tag1", "tag2", "tag3"))
                .build();

        String postDtoJson = objectMapper.writeValueAsString(postRequestDto);

        given(postService.createPost(any(PostRequestDto.class), anyLong(), eq(null)))
                .willReturn(new PostResponseDto());

        MockMultipartFile jsonFile = new MockMultipartFile("post", "", "application/json", postDtoJson.getBytes());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart("/posts/{communityId}", 1L)
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreatePost_WhenCommunityNotFound_ThrowsResourceNotFoundException() throws Exception {
        PostRequestDto postRequestDto = PostRequestDto.builder()
                .title("test title")
                .content("test content")
                .tags(Set.of("tag1", "tag2", "tag3"))
                .build();

        String postDtoJson = objectMapper.writeValueAsString(postRequestDto);

        given(postService.createPost(any(PostRequestDto.class), anyLong(), any(MultipartFile.class)))
                .willThrow(new ResourceNotFoundException("Community not found."));

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[0]);
        MockMultipartFile jsonFile = new MockMultipartFile("post", "", "application/json", postDtoJson.getBytes());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart("/posts/{communityId}", 1L)
                .file(imageFile)
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdatePostWithFile_Success() throws Exception {
        PostRequestDto postRequestDto = PostRequestDto.builder()
                .title("new title")
                .content("new content")
                .tags(Set.of("tag1", "tag2", "tag3"))
                .build();

        String postDtoJson = objectMapper.writeValueAsString(postRequestDto);

        given(postService.updatePost(any(UpdatePostRequestDto.class), anyLong(), any(MultipartFile.class)))
                .willReturn(new PostResponseDto());

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes());
        MockMultipartFile jsonFile = new MockMultipartFile("post", "", "application/json", postDtoJson.getBytes());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(HttpMethod.PUT, "/posts/{postId}", 1L)
                .file(imageFile)
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdatePostWithOutFile_Success() throws Exception {
        PostRequestDto postRequestDto = PostRequestDto.builder()
                .title("new title")
                .content("new content")
                .tags(Set.of("tag1", "tag2", "tag3"))
                .build();

        String postDtoJson = objectMapper.writeValueAsString(postRequestDto);

        given(postService.updatePost(any(UpdatePostRequestDto.class), anyLong(), eq(null)))
                .willReturn(new PostResponseDto());

        MockMultipartFile jsonFile = new MockMultipartFile("post", "", "application/json", postDtoJson.getBytes());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(HttpMethod.PUT, "/posts/{postId}", 1L)
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdatePost_WhenPostNotFound_ThrowsResourceNotFoundException() throws Exception {
        PostRequestDto postRequestDto = PostRequestDto.builder()
                .title("test title")
                .content("test content")
                .tags(Set.of("tag1", "tag2", "tag3"))
                .build();

        String postDtoJson = objectMapper.writeValueAsString(postRequestDto);

        given(postService.updatePost(any(UpdatePostRequestDto.class), anyLong(), any(MultipartFile.class)))
                .willThrow(new ResourceNotFoundException("Post not found."));

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[0]);
        MockMultipartFile jsonFile = new MockMultipartFile("post", "", "application/json", postDtoJson.getBytes());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(HttpMethod.PUT,"/posts/{postId}", 1L)
                .file(imageFile)
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSavePost_Success_ReturnsMessageDto() throws Exception {
        given(postService.savePost(anyLong())).willReturn(new MessageDto("Post saved."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/posts/save/{postId}", 1L)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());
    }

    @Test
    public void testSavePost_WhenPostNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(postService.savePost(anyLong())).willThrow(new ResourceNotFoundException("Post not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/posts/save/{postId}", 1L)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUnSavePost_Success_ReturnsMessageDto() throws Exception {
        given(postService.savePost(anyLong())).willReturn(new MessageDto("Post unsaved."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/posts/unsave/{postId}", 1L)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUnSavePost_WhenPostNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(postService.unSavePost(anyLong())).willThrow(new ResourceNotFoundException("Post not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/posts/unsave/{postId}", 1L)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeletePost_Success_ReturnsMessageDto() throws Exception {
        given(postService.deletePost(anyLong())).willReturn(new MessageDto());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/posts/{postId}", 1L)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeletePost_WhenPostNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(postService.deletePost(anyLong())).willThrow(new ResourceNotFoundException("Post not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/posts/{postId}", 1L)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }
}
