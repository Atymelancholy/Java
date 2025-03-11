package com.example.bookblog.dto;

import com.example.bookblog.entity.User;
import java.util.List;

public class UserWithResponsesAndCategoryDto {
    private Long id;
    private String username;
    private List<ResponseDto> posts;
    private List<CategoryDto> groups;

    public static UserWithResponsesAndCategoryDto toModel(User entity) {
        UserWithResponsesAndCategoryDto model = new UserWithResponsesAndCategoryDto();
        model.setId(entity.getId());
        model.setUsername(entity.getUsername());

        model.setPosts(entity.getResponses().stream()
                .map(ResponseDto::toModel)
                .toList());

        model.setGroups(entity.getCategories().stream()
                .map(CategoryDto::toModel)
                .toList());
        return model;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<ResponseDto> getPosts() {
        return posts;
    }

    public void setPosts(List<ResponseDto> posts) {
        this.posts = posts;
    }

    public List<CategoryDto> getGroups() {
        return groups;
    }

    public void setGroups(List<CategoryDto> groups) {
        this.groups = groups;
    }
}
