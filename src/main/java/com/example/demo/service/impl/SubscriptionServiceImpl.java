package com.example.demo.service.impl;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.response.SubscriptionResponseDto;
import com.example.demo.entity.Community;
import com.example.demo.entity.Subscription;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.service.ISubscriptionService;
import com.example.demo.service.IUtilService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements ISubscriptionService {
    private final IUtilService utilService;
    private final SubscriptionRepository subscriptionRepository;
    private final CommunityRepository communityRepository;
    private final ModelMapper modelMapper;

    public boolean getSubscription(Long communityId) {
        User user = utilService.getCurrentUser();
        return subscriptionRepository.existsByCommunityIdAndUserId(communityId, user.getId());
    }

    public Long getSubscriptionsCountByCommunity(Long communityId) {
        return subscriptionRepository.countSubscriptionsByCommunityId(communityId);
    }

    public SubscriptionResponseDto createSubscription(Long communityId) {
        User user = utilService.getCurrentUser();
        Optional<Subscription> optionalSubscription = subscriptionRepository
                .findByCommunityIdAndUserId(communityId, user.getId());
        ;

        if (optionalSubscription.isEmpty()) {
            Community community = communityRepository.findById(communityId)
                    .orElseThrow(() -> new ResourceNotFoundException("Community not found."));
            Subscription subscription = Subscription.builder()
                    .user(user)
                    .community(community)
                    .build();
            subscriptionRepository.save(subscription);
            return modelMapper.map(subscription, SubscriptionResponseDto.class);
        } else {
            return modelMapper.map(optionalSubscription, SubscriptionResponseDto.class);
        }
    }

    @Transactional
    public MessageDto deleteSubscription(Long communityId) {
        User user = utilService.getCurrentUser();
        Subscription subscription = subscriptionRepository
                .findByCommunityIdAndUserId(communityId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found."));

        subscriptionRepository
                .deleteByCommunityIdAndUserId(communityId, user.getId());
        return new MessageDto("Unsubscribed from community " + subscription.getCommunity().getName());
    }
}
