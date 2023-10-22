package com.example.demo.controller;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.service.ISubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final ISubscriptionService subscriptionService;


    @GetMapping("/{communityId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Boolean> getSubscription(
            @PathVariable("communityId") Long communityId
    ) {
        return ResponseEntity.ok(subscriptionService.getSubscription(communityId));
    }

    @GetMapping("/{communityId}/count")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Long> getSubscriptionsCountByCommunity(
            @PathVariable("communityId") Long communityId
    ) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsCountByCommunity(communityId));
    }

    @PostMapping("/subscribe/{communityId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> createSubscription(
            @PathVariable("communityId") Long communityId
    ) {
        return ResponseEntity.ok(subscriptionService.createSubscription(communityId));
    }

    @DeleteMapping("/unsubscribe/{communityId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageDto> deleteSubscription(
            @PathVariable("communityId") Long communityId
    ) {
        return ResponseEntity.ok(subscriptionService.deleteSubscription(communityId));
    }
}

