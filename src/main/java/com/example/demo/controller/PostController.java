package com.example.demo.controller;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.PostRequestDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.PostResponseDto;
import com.example.demo.service.IPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
public class PostController {
    private final IPostService postService;

    @GetMapping("/page/{page}")
    public ResponseEntity<PageDto<PostResponseDto>> getPosts(
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(postService.getPosts(page));
    }

    @GetMapping("/{postId}")
    public PostResponseDto getPost(
            @PathVariable("postId") Long postId
    ) {
        return postService.getPost(postId);
    }

    @GetMapping("/users/{userId}/page/{page}")
    public ResponseEntity<PageDto<PostResponseDto>> getUserPosts(
            @PathVariable("userId") Long userId,
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(postService.getUserPosts(userId, page));
    }

    @GetMapping("/saved/page/{page}")
    public ResponseEntity<PageDto<PostResponseDto>> getUserSavedPosts(
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(postService.getUserSavedPosts(page));
    }

    @GetMapping("/subscribed/page/{page}")
    public ResponseEntity<PageDto<PostResponseDto>> getUserSubscribedCommunitiesPosts(
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(postService.getUserSubscribedCommunitiesPosts(page));
    }

    @GetMapping("/upvoted/{userId}/page/{page}")
    public ResponseEntity<PageDto<PostResponseDto>> getUserUpVotedPosts(
            @PathVariable("userId") Long userId,
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(postService.getUserUpVotedPosts(page));
    }

    @GetMapping("/downvoted/{userId}/page/{page}")
    public ResponseEntity<PageDto<PostResponseDto>> getUserDownVotedPosts(
            @PathVariable("userId") Long userId,
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(postService.getUserDownVotedPosts(page));
    }

    @GetMapping("/tag/{tagName}/page/{page}")
    public ResponseEntity<PageDto<PostResponseDto>> getPostsByTag(
            @PathVariable("tagName") String tagName,
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(postService.getPostsByTag(tagName, page));
    }

    @GetMapping("/communities/{communityId}/page/{page}")
    public ResponseEntity<PageDto<PostResponseDto>> getCommunityPosts(
            @PathVariable("communityId") Long communityId,
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(postService.getPostsByCommunity(communityId, page));
    }

    @GetMapping("is-saved/{postId}")
    public ResponseEntity<Boolean> isPostSaved(
            @PathVariable("postId") Long postId
    ) {
        return ResponseEntity.ok(postService.isPostSaved(postId));
    }

    @PostMapping(value = "/{communityId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PostResponseDto createPost(
            @PathVariable("communityId") Long communityId,
            @RequestPart(value = "post") @Valid PostRequestDto postRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {
        return postService.createPost(postRequestDto, communityId, file);
    }

    @PostMapping("/save/{postId}")
    public ResponseEntity<MessageDto> savePost(
            @PathVariable("postId") Long postId
    ) {
        return ResponseEntity.ok(postService.savePost(postId));
    }

    @DeleteMapping("/unsave/{postId}")
    public ResponseEntity<MessageDto> unSavePost(
            @PathVariable("postId") Long postId
    ) {
        return ResponseEntity.ok(postService.unSavePost(postId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<MessageDto> deletePost(
            @PathVariable("postId") Long postId
    ) {
        return ResponseEntity.ok(postService.deletePost(postId));
    }
}

