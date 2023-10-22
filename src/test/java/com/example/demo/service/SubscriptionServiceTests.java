package com.example.demo.service;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.response.SubscriptionResponseDto;
import com.example.demo.entity.Community;
import com.example.demo.entity.Subscription;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.service.impl.SubscriptionServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTests {
    @Mock
    private IUtilService utilService;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private CommunityRepository communityRepository;
    @Spy
    private ModelMapper modelMapper;
    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private User user;
    private Community community;
    private Subscription subscription;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .username("author")
                .email("test@test.com")
                .password("test")
                .build();

        community = Community.builder()
                .id(1L)
                .name("testcommunity")
                .creator(user)
                .build();

        subscription = Subscription.builder()
                .user(user)
                .community(community)
                .build();
    }

    @Test
    void testGetSubscription_WhenSubscriptionExists_ReturnsTrue() {
        given(subscriptionRepository.existsByCommunityIdAndUserId(anyLong(), anyLong())).willReturn(true);
        given(utilService.getCurrentUser()).willReturn(user);

        boolean result = subscriptionService.getSubscription(1L);

        assertTrue(result);
        verify(subscriptionRepository, times(1)).existsByCommunityIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void testGetSubscription_WhenSubscriptionDoesNotExist_ReturnsFalse() {
        given(subscriptionRepository.existsByCommunityIdAndUserId(anyLong(), anyLong())).willReturn(false);
        given(utilService.getCurrentUser()).willReturn(user);

        boolean result = subscriptionService.getSubscription(1L);

        assertFalse(result);
        verify(subscriptionRepository, times(1)).existsByCommunityIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void testGetSubscriptionsCountByCommunity() {
        given(subscriptionRepository.countSubscriptionsByCommunityId(anyLong())).willReturn(anyLong());

        Long result = subscriptionService.getSubscriptionsCountByCommunity(1L);

        assertThat(result).isInstanceOf(Long.class);
        verify(subscriptionRepository, times(1))
                .countSubscriptionsByCommunityId(anyLong());
    }

    @Test
    void testCreateSubscription_WhenSubscriptionExists_ReturnsExistentSubscription() {
        given(utilService.getCurrentUser()).willReturn(user);
        given(subscriptionRepository.findByCommunityIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(subscription));

        SubscriptionResponseDto result = subscriptionService.createSubscription(community.getId());

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(SubscriptionResponseDto.class);
        verify(subscriptionRepository, times(1))
                .findByCommunityIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void testCreateSubscription_WhenSubscriptionExists_ReturnsNewSubscription() {
        given(utilService.getCurrentUser()).willReturn(user);
        given(subscriptionRepository.findByCommunityIdAndUserId(anyLong(), anyLong())).willReturn(Optional.empty());
        given(communityRepository.findById(anyLong())).willReturn(Optional.of(community));

        SubscriptionResponseDto result = subscriptionService.createSubscription(1L);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(SubscriptionResponseDto.class);
        verify(subscriptionRepository, times(1))
                .findByCommunityIdAndUserId(community.getId(), user.getId());
        verify(subscriptionRepository, times(1))
                .save(any(Subscription.class));
        verify(communityRepository, times(1))
                .findById(community.getId());
    }

    @Test
    void testCreateSubscription_WhenCommunityNotFound_ThrowsResourceNotFoundException() {
        given(utilService.getCurrentUser()).willReturn(user);
        given(subscriptionRepository.findByCommunityIdAndUserId(anyLong(), anyLong())).willReturn(Optional.empty());
        given(communityRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                subscriptionService.createSubscription(1L));

        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void testDeleteSubscription_WhenSubscriptionExists_ReturnsMessageDto() {
        given(utilService.getCurrentUser()).willReturn(user);
        given(subscriptionRepository.findByCommunityIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(subscription));

        MessageDto result = subscriptionService.deleteSubscription(1L);

        assertThat(result).isInstanceOf(MessageDto.class);
        verify(subscriptionRepository, times(1)).deleteByCommunityIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void testDeleteSubscription_WhenSubscriptionDoesNotExist_ThrowsResourceNotFoundException() {
        given(utilService.getCurrentUser()).willReturn(user);
        given(subscriptionRepository.findByCommunityIdAndUserId(anyLong(), anyLong())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                subscriptionService.deleteSubscription(1L));

        verify(subscriptionRepository, never()).deleteByCommunityIdAndUserId(anyLong(), anyLong());
    }
}
