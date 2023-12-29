package com.example.demo.controller;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.response.SubscriptionResponseDto;
import com.example.demo.service.ISubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Subscription", description = "Endpoints related to subscriptions")
@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class SubscriptionController {
    private final ISubscriptionService subscriptionService;

    @Operation(summary = "Check if the current user is subscribed to a community")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Checked if the user is subscribed to a community",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))
                    }
            )
    })
    @GetMapping("/{communityId}")
    public ResponseEntity<Boolean> getSubscription(
            @PathVariable("communityId") Long communityId
    ) {
        return ResponseEntity.ok(subscriptionService.getSubscription(communityId));
    }

    @Operation(summary = "Get number of community members")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returned the number of members in a community",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Long.class))
                    }
            )
    })
    @GetMapping("/{communityId}/count")
    public ResponseEntity<Long> getSubscriptionsCountByCommunity(
            @PathVariable("communityId") Long communityId
    ) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsCountByCommunity(communityId));
    }

    @Operation(summary = "Subscribe to a community")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Subscribed to community",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = SubscriptionResponseDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Community not found"
            )
    })
    @PostMapping("/subscribe/{communityId}")
    public ResponseEntity<SubscriptionResponseDto> createSubscription(
            @PathVariable("communityId") Long communityId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionService.createSubscription(communityId));
    }

    @Operation(summary = "Unsubscribe from a community")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Unsubscribed from community",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = MessageDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Subscription not found"
            )
    })
    @DeleteMapping("/unsubscribe/{communityId}")
    public ResponseEntity<MessageDto> deleteSubscription(
            @PathVariable("communityId") Long communityId
    ) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(subscriptionService.deleteSubscription(communityId));
    }
}

