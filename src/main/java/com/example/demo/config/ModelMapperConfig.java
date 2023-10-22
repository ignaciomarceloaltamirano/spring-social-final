package com.example.demo.config;

import com.example.demo.dto.response.*;
import com.example.demo.entity.*;
import com.example.demo.enumeration.ERole;
import com.example.demo.enumeration.EVoteType;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        TypeMap<Community, CommunityResponseDto> communityTypeMap = modelMapper
                .createTypeMap(Community.class, CommunityResponseDto.class);
        communityTypeMap.addMapping(src -> src.getCreator().getId(), CommunityResponseDto::setCreatorId);
        communityTypeMap.addMapping(src -> src.getCreator().getUsername(), CommunityResponseDto::setCreatorName);

        TypeMap<Post, PostResponseDto> postTypeMap = modelMapper
                .createTypeMap(Post.class, PostResponseDto.class);
        postTypeMap.addMapping(src -> src.getAuthor().getId(), PostResponseDto::setAuthorId);
        postTypeMap.addMapping(src -> src.getAuthor().getUsername(), PostResponseDto::setAuthorName);
        postTypeMap.addMapping(src -> src.getCommunity().getId(), PostResponseDto::setCommunityId);
        postTypeMap.addMapping(src -> src.getCommunity().getName(), PostResponseDto::setCommunityName);
        postTypeMap.addMappings(src -> src.using(new TagsToStringsConverter())
                .map(Post::getTags, PostResponseDto::setTags));

        TypeMap<Vote, VoteResponseDto> voteTypeMap = modelMapper
                .createTypeMap(Vote.class, VoteResponseDto.class);
        voteTypeMap.addMappings(mapper -> mapper.using(new EVoteTypeToStringConverter())
                .map(Vote::getType, VoteResponseDto::setType));

        TypeMap<Comment, CommentResponseDto> commentTypeMap = modelMapper
                .createTypeMap(Comment.class, CommentResponseDto.class);
        commentTypeMap.addMapping(src -> src.getAuthor().getId(), CommentResponseDto::setAuthorId);
        commentTypeMap.addMapping(src -> src.getAuthor().getUsername(), CommentResponseDto::setAuthorName);
        commentTypeMap.addMapping(src -> src.getPost().getId(), CommentResponseDto::setPostId);
        commentTypeMap.addMapping(src -> src.getReplyTo().getId(), CommentResponseDto::setReplyToId);

        TypeMap<User, UserResponseDto> userTypeMap = modelMapper.createTypeMap(User.class, UserResponseDto.class);
        userTypeMap.addMappings(mapping -> mapping.using(new RolesToStringsConverter())
                .map(User::getRoles, UserResponseDto::setRoles));

        TypeMap<Subscription,SubscriptionResponseDto> subscriptionTypeMap= modelMapper
                .createTypeMap(Subscription.class, SubscriptionResponseDto.class);
        subscriptionTypeMap.addMapping(src->src.getUser().getId(),SubscriptionResponseDto::setUserId);
        subscriptionTypeMap.addMapping(src->src.getUser().getUsername(),SubscriptionResponseDto::setUserName);
        subscriptionTypeMap.addMapping(src->src.getCommunity().getId(),SubscriptionResponseDto::setCommunityId);
        subscriptionTypeMap.addMapping(src->src.getCommunity().getName(),SubscriptionResponseDto::setCommunityName);
        return modelMapper;
    }

    public static class RolesToStringsConverter extends AbstractConverter<Set<Role>, List<ERole>> {
        protected List<ERole> convert(Set<Role> roles) {
            return roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());
        }
    }

    public static class TagsToStringsConverter extends AbstractConverter<Set<Tag>, Set<String>> {
        protected Set<String> convert(Set<Tag> tags) {
            return tags.stream()
                    .map(Tag::getName).collect(Collectors.toSet());
        }
    }

    public static class EVoteTypeToStringConverter extends AbstractConverter<EVoteType, String> {
        protected String convert(EVoteType source) {
            return source.name();
        }
    }
}

