package com.example.demo.repository;

import com.example.demo.entity.Community;
import com.example.demo.entity.Subscription;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SubscriptionRepositoryTests {
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommunityRepository communityRepository;

    private User user;
    private Community community;

    @BeforeEach
    void setup() {
        user = User.builder()
                .username("author")
                .email("test@test.com")
                .password("test")
                .build();
        userRepository.save(user);

        community = Community.builder().name("testcommunity").build();
        communityRepository.save(community);

        Subscription subscription = Subscription.builder()
                .user(user)
                .community(community)
                .build();
        subscriptionRepository.save(subscription);
    }

    @Test
    void testSaveSubscription() {
        Subscription subscription = Subscription.builder()
                .user(user)
                .community(community)
                .build();
        subscriptionRepository.save(subscription);

        Subscription retrievedSubscription = subscriptionRepository.findById(subscription.getId()).get();

        assertThat(retrievedSubscription.getId()).isGreaterThan(0);
        assertThat(retrievedSubscription.getUser()).isEqualTo(user);
        assertThat(retrievedSubscription.getCommunity()).isEqualTo(community);
        assertThat(subscriptionRepository.count()).isGreaterThan(0);
    }

    @Test
    void testFindByCommunityAndUser_Success() {
        Subscription retrievedSubscription = subscriptionRepository.findByCommunityIdAndUserId(
                community.getId(), user.getId()).get();

        assertThat(retrievedSubscription).isNotNull();
        assertThat(retrievedSubscription.getCommunity().getName()).isEqualTo("testcommunity");
    }

    @Test
    void testFindByCommunityAndUser_WhenSubscriptionNotFound_ThrowsResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () -> subscriptionRepository
                .findByCommunityIdAndUserId(-1L,-1L)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found")));
    }

    @Test
    void testGetSubscriptionsCountByCommunity() {
        Long subscriptionsCount = subscriptionRepository.countSubscriptionsByCommunityId(community.getId());

        assertThat(subscriptionsCount).isGreaterThan(0);
    }

    @Test
    void testExistsByCommunityIdAndUserId() {
        boolean exists = subscriptionRepository.existsByCommunityIdAndUserId(community.getId(), user.getId());
        System.out.println(community.getId());
        System.out.println(user.getId());
        assertTrue(exists);
    }

    @Test
    void testDoesNotExistByCommunityIdAndUserId() {
        boolean exists = subscriptionRepository.existsByCommunityIdAndUserId(-1L, -1L);
        assertFalse(exists);
    }
}
