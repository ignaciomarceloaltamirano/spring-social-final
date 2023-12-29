package com.example.demo.controller;

import com.example.demo.dto.request.UpdatePasswordRequestDto;
import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.UpdateUserRequestDto;
import com.example.demo.dto.response.UpdateUserResponseDto;
import com.example.demo.dto.response.UserProfileResponseDto;
import com.example.demo.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "User", description = "Endpoints related to users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class UserController {
    private final IUserService userService;

    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a list of users",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = UserProfileResponseDto.class))
                    }
            )
    })
    @GetMapping
    public ResponseEntity<List<UserProfileResponseDto>> getUsers(
    ) {
        return ResponseEntity.ok(userService.getUsers());
    }

    @Operation(summary = "Get user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a user",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = UserProfileResponseDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponseDto> getUser(
            @PathVariable("username") String username
    ) {
        return ResponseEntity.ok(userService.getUser(username));
    }

    @Operation(summary = "Update a user information")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Updated a user's information",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = UpdateUserResponseDto.class))
                    }
            )
    })
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UpdateUserResponseDto> updateUser(
            @RequestPart(value = "user", required = false) @Parameter(schema =@Schema(type = "string", format = "binary")) @Valid UpdateUserRequestDto updateUserRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(userService.updateUser(updateUserRequestDto, file));
    }

    @Operation(summary = "Update a user password")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Updated a user's password",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = MessageDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized: Wrong password"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request: Passwords don't match"
            )
    })
    @PutMapping("/update-password")
    public ResponseEntity<MessageDto> updatePassword(
            @RequestBody UpdatePasswordRequestDto updatePasswordRequestDto
    ) {
        return ResponseEntity.ok(userService.updatePassword(updatePasswordRequestDto));
    }
}
