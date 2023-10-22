package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionResponseDto {
    private Long id;
    private Long userId;
    private String userName;
    private Long communityId;
    private String communityName;
}
