package com.example.bookblog.service;

import com.example.bookblog.dto.CategoryWithUsersDto;
import com.example.bookblog.entity.Category;
import com.example.bookblog.entity.User;
import com.example.bookblog.exception.CategoryAlreadyExistException;
import com.example.bookblog.exception.CategoryNotFoundException;
import com.example.bookblog.repository.CategoryRepository;
import com.example.bookblog.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public Category registration(Category group) throws CategoryAlreadyExistException {
        if (categoryRepository.findByName(group.getName()) != null) {
            throw new CategoryAlreadyExistException("Group with this name already exists!!!");
        }
        return categoryRepository.save(group);
    }

    public CategoryWithUsersDto getOne(Long id) throws CategoryNotFoundException {
        Category group = categoryRepository.findWithUsersById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Group with this id not exist!"));
        return CategoryWithUsersDto.toModel(group);
    }

    @Transactional
    public void deleteGroup(Long groupId) throws CategoryNotFoundException {
        Category group = categoryRepository.findById(groupId)
                .orElseThrow(() -> new CategoryNotFoundException("Group not found"));

        Set<User> users = new HashSet<>(group.getUsers());

        for (User user : users) {
            user.removeGroup(group);
            userRepository.save(user);
        }

        categoryRepository.delete(group);
    }

    public void updateGroup(Long id, Category updatedGroup) throws CategoryNotFoundException {
        Category existingGroup = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Group with this ID does not exist!!!"));
        existingGroup.setName(updatedGroup.getName());

        categoryRepository.save(existingGroup);
    }
}
