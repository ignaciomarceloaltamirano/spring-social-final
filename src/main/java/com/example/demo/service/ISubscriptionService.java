package com.example.demo.service;

import com.example.demo.auth.dto.response.MessageDto;

public interface ISubscriptionService {
    boolean getSubscription(Long communityId);
    Object createSubscription(Long communityId);
    MessageDto deleteSubscription(Long communityId);
    Long getSubscriptionsCountByCommunity(Long communityId);
}
