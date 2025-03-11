package com.example.bookblog.service;

import com.example.bookblog.dto.UserWithResponsesAndCategoryDto;
import com.example.bookblog.entity.Category;
import com.example.bookblog.entity.User;
import com.example.bookblog.exception.CategoryNotFoundException;
import com.example.bookblog.exception.UserAlreadyExistException;
import com.example.bookblog.exception.UserNotFoundException;
import com.example.bookblog.repository.CategoryRepository;
import com.example.bookblog.repository.UserRepository;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CategoryRepository groupRepository;

    @Autowired
    public UserService(UserRepository userRepository, CategoryRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    public User registration(User user) throws UserAlreadyExistException {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new UserAlreadyExistException("User with this name already exists!!!");
        }
        return userRepository.save(user);
    }

    public UserWithResponsesAndCategoryDto getOne(Long id) throws UserNotFoundException {
        User user = userRepository.findWithPostsAndGroupsById(id)
                .orElseThrow(() -> new UserNotFoundException("User with this id not exist!!!"));
        return UserWithResponsesAndCategoryDto.toModel(user);
    }

    public Long delete(Long id) throws UserNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with this id does not exist!!!");
        }
        userRepository.deleteById(id);
        return id;
    }

    public User getUserById(Long id) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new UserNotFoundException("User with this id not exist!!!");
        }
    }

    public void addUserToGroup(Long userId, Long groupId) throws UserNotFoundException,
            CategoryNotFoundException {
        User user = getUserById(userId);
        Category group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Group with this id not exist!!!"));

        user.addGroup(group);
        userRepository.save(user);
    }

    public void removeUserFromGroup(Long userId, Long groupId) throws UserNotFoundException,
            CategoryNotFoundException {
        User user = getUserById(userId);
        Category group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Group with this id not exist!!!"));
        user.removeGroup(group);
        userRepository.save(user);
    }

    public Set<Category> getUserGroups(Long userId) throws UserNotFoundException {
        return getUserById(userId).getCategories();
    }

    public void updateUser(Long id, User updatedUser) throws UserNotFoundException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with this ID not exist!!!"));

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setPassword(updatedUser.getPassword());

        userRepository.save(existingUser);
    }
}

