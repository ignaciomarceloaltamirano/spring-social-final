package com.example.demo.repository;

import com.example.demo.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {
    Long countSubscriptionsByCommunityId(Long communityId);
    boolean existsByCommunityIdAndUserId(Long communityId, Long userId);
    Optional<Subscription> findByCommunityIdAndUserId(Long communityId, Long userId);
    void deleteByCommunityIdAndUserId(Long communityId, Long userId);
}
