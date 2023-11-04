package com.example.demo.service;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.response.SubscriptionResponseDto;

public interface ISubscriptionService {
    boolean getSubscription(Long communityId);
    SubscriptionResponseDto createSubscription(Long communityId);
    MessageDto deleteSubscription(Long communityId);
    Long getSubscriptionsCountByCommunity(Long communityId);
}
