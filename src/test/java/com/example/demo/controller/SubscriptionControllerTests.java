package com.example.demo.controller;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.auth.service.JwtService;
import com.example.demo.auth.service.UserDetailsServiceImpl;
import com.example.demo.dto.response.SubscriptionResponseDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.service.IUtilService;
import com.example.demo.service.impl.SubscriptionServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubscriptionController.class)
@WithMockUser(username = "user", password = "test", roles = {"USER", "MOD", "ADMIN"})
@AutoConfigureMockMvc(addFilters = false)
public class SubscriptionControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SubscriptionServiceImpl subscriptionService;
    @MockBean
    private CommunityRepository communityRepository;
    @MockBean
    private SubscriptionRepository subscriptionRepository;
    @MockBean
    private IUtilService utilService;
    @MockBean
    private ModelMapper modelMapper;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void testGetSubscription_ReturnsTrue() throws Exception {
        given(subscriptionService.getSubscription(anyLong()))
                .willReturn(true);

        RequestBuilder requestBuilder= MockMvcRequestBuilders
                .get("/subscriptions/{communityId}",anyLong())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testGetSubscription_ReturnsFalse() throws Exception {
        given(subscriptionService.getSubscription(anyLong()))
                .willReturn(false);

        RequestBuilder requestBuilder= MockMvcRequestBuilders
                .get("/subscriptions/{communityId}",anyLong())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testGetSubscriptionsCount_Success() throws Exception {
        given(subscriptionService.getSubscriptionsCountByCommunity(1L))
                .willReturn(1L);

        RequestBuilder requestBuilder= MockMvcRequestBuilders
                .get("/subscriptions/{communityId}/count",anyLong())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testCreateSubscription_Success_ReturnsNewSubscription() throws Exception {
        given(subscriptionService.createSubscription(anyLong()))
                .willReturn(SubscriptionResponseDto.builder().communityName("test").build());

        RequestBuilder requestBuilder= MockMvcRequestBuilders
                .post("/subscriptions/subscribe/{communityId}",anyLong())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.communityName").value("test"))
                .andReturn();
    }

    @Test
    public void testCreateSubscription_WhenCommunityNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(subscriptionService.createSubscription(anyLong()))
                .willThrow(new ResourceNotFoundException("Community not found"));

        RequestBuilder requestBuilder= MockMvcRequestBuilders
                .post("/subscriptions/subscribe/{communityId}",anyLong())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andReturn();
    }
    @Test
    public void testDeleteSubscription_Success_ReturnsMessageDto() throws Exception {
        given(subscriptionService.deleteSubscription(anyLong()))
                .willReturn(new MessageDto("Unsubscribed from community"));

        RequestBuilder requestBuilder= MockMvcRequestBuilders
                .delete("/subscriptions/unsubscribe/{communityId}",anyLong())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Unsubscribed from community"))
                .andReturn();
    }

    @Test
    public void testDeleteSubscription_WhenSubscriptionNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(subscriptionService.deleteSubscription(anyLong()))
                .willThrow(new ResourceNotFoundException("Subscription not found"));

        RequestBuilder requestBuilder= MockMvcRequestBuilders
                .delete("/subscriptions/unsubscribe/{communityId}",anyLong())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
