package com.example.demo.controller;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.UpdatePostRequestDto;
import com.example.demo.dto.request.PostRequestDto;
import com.example.demo.dto.response.DeletePostResponseDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.PostResponseDto;
import com.example.demo.service.IPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Post", description = "Endpoints related to posts")
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class PostController {
    private final IPostService postService;

    @Operation(summary = "Get a post")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a post",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = PostResponseDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found"
            )
    })
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPost(
            @PathVariable("postId") Long postId
    ) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @Operation(summary = "Get all posts")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a page of posts",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageDto.class,
                                    subTypes = {PostResponseDto.class}))
                    }
            )
    })
    @GetMapping("/page/{page}")
    public ResponseEntity<PageDto<PostResponseDto>> getPosts(
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(postService.getPosts(page));
    }

    @Operation(summary = "Get all user's posts")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a page of a user's posts",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageDto.class,
                                    subTypes = {PostResponseDto.class}))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping("/users/{userId}/page/{page}")
    public ResponseEntity<PageDto<PostResponseDto>> getUserPosts(
            @PathVariable("userId") Long userId,
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(postService.getUserPosts(userId, page));
    }

    @Operation(summary = "Get all user's saved posts")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a page of a user's saved posts",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageDto.class,
                                    subTypes = {PostResponseDto.class}))
                    }
            )
    })
    @GetMapping("/saved/page/{page}")
    public ResponseEntity<PageDto<PostResponseDto>> getUserSavedPosts(
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(postService.getUserSavedPosts(page));
    }

    @Operation(summary = "Get all user's subscribed communities posts")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a page of a user's subscribed communities posts",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageDto.class,
                                    subTypes = {PostResponseDto.class}))
                    }
            )
    })
    @GetMapping("/subscribed/page/{page}")
    public ResponseEntity<PageDto<PostResponseDto>> getUserSubscribedCommunitiesPosts(
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(postService.getUserSubscribedCommunitiesPosts(page));
    }

    @Operation(summary = "Get all user's upvoted posts")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a page of a user's upvoted posts",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageDto.class,
                                    subTypes = {PostResponseDto.class}))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping("/upvoted/{userId}/page/{page}")
    public ResponseEntity<PageDto<PostResponseDto>> getUserUpVotedPosts(
            @PathVariable("userId") Long userId,
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(postService.getUserUpVotedPosts(userId, page));
    }

    @Operation(summary = "Get all user's downvoted posts")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a page of a user's downvoted posts",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageDto.class,
                                    subTypes = {PostResponseDto.class}))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping("/downvoted/{userId}/page/{page}")
    public ResponseEntity<PageDto<PostResponseDto>> getUserDownVotedPosts(
            @PathVariable("userId") Long userId,
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(postService.getUserDownVotedPosts(userId, page));
    }

    @Operation(summary = "Get posts by tag")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a page of posts",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageDto.class,
                                    subTypes = {PostResponseDto.class}))
                    }
            )
    })
    @GetMapping("/tag/{tagName}/page/{page}")
    public ResponseEntity<PageDto<PostResponseDto>> getPostsByTag(
            @PathVariable("tagName") String tagName,
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(postService.getPostsByTag(tagName, page));
    }

    @Operation(summary = "Get all community's posts")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a page of a community's posts",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageDto.class,
                                    subTypes = {PostResponseDto.class}))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Community not found"
            )
    })
    @GetMapping("/communities/{communityId}/page/{page}")
    public ResponseEntity<PageDto<PostResponseDto>> getCommunityPosts(
            @PathVariable("communityId") Long communityId,
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(postService.getPostsByCommunity(communityId, page));
    }

    @Operation(summary = "Check if the user has saved a post")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Checked if the user has saved a post",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found"
            )
    })
    @GetMapping("is-saved/{postId}")
    public ResponseEntity<Boolean> isPostSaved(
            @PathVariable("postId") Long postId
    ) {
        return ResponseEntity.ok(postService.isPostSaved(postId));
    }

    @Operation(summary = "Create a post")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created a post",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = PostResponseDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Community not found"
            )
    })
    @PostMapping(value = "/{communityId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponseDto> createPost(
            @PathVariable("communityId") Long communityId,
            @RequestPart(value = "post") @Parameter(schema =@Schema(type = "string", format = "binary")) @Valid PostRequestDto post,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(post, communityId, file));
    }

    @Operation(summary = "Update a post")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Updated a post",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = PostResponseDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found"
            )
    })
    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable("postId") Long postId,
            @RequestPart(value = "post", required = false) @Parameter(schema =@Schema(type = "string", format = "binary")) @Valid UpdatePostRequestDto updatePostRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(postService.updatePost(updatePostRequestDto, postId, file));
    }

    @Operation(summary = "Save a post")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Saved a post",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = MessageDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found"
            )
    })
    @PostMapping("/save/{postId}")
    public ResponseEntity<MessageDto> savePost(
            @PathVariable("postId") Long postId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.savePost(postId));
    }

    @Operation(summary = "Unsave a post")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Unsaved a post",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = MessageDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found"
            )
    })
    @DeleteMapping("/unsave/{postId}")
    public ResponseEntity<MessageDto> unSavePost(
            @PathVariable("postId") Long postId
    ) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(postService.unSavePost(postId));
    }

    @Operation(summary = "Delete a post")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Deleted a post",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = DeletePostResponseDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found"
            )
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<DeletePostResponseDto> deletePost(
            @PathVariable("postId") Long postId
    ) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(postService.deletePost(postId));
    }
}

