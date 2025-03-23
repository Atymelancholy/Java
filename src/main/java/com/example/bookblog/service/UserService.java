package com.example.bookblog.service;

import com.example.bookblog.dto.UserWithResponsesAndCategoryDto;
import com.example.bookblog.entity.Category;
import com.example.bookblog.entity.User;
import com.example.bookblog.exception.CategoryNotFoundException;
import com.example.bookblog.exception.UserAlreadyExistException;
import com.example.bookblog.exception.UserNotFoundException;
import com.example.bookblog.exception.ValidationException;
import com.example.bookblog.repository.CategoryRepository;
import com.example.bookblog.repository.UserRepository;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CategoryRepository groupRepository;
    private final ConcurrentHashMap<Long, UserWithResponsesAndCategoryDto> userCache
            = new ConcurrentHashMap<>();

    @Autowired
    public UserService(UserRepository userRepository, CategoryRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    public User registration(User user) throws UserAlreadyExistException {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new ValidationException("Username must not be empty");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password must not be empty");
        }

        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new UserAlreadyExistException("User with this name already exists!!!");
        }
        return userRepository.save(user);
    }

    public UserWithResponsesAndCategoryDto getOne(Long id) throws UserNotFoundException {
        User user = userRepository.findWithPostsAndGroupsById(id)
                .orElseThrow(() -> new UserNotFoundException("User with this id not exist!!!"));

        UserWithResponsesAndCategoryDto dto = UserWithResponsesAndCategoryDto.toModel(user);
        userCache.put(id, dto);

        return dto;
    }

    public User getUserById(Long id) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new UserNotFoundException("User with this id not exist!!!");
        }
    }

    public void addUserToGroup(Long userId, Long groupId)
            throws UserNotFoundException, CategoryNotFoundException {
        User user = getUserById(userId);
        Category group = groupRepository.findById(groupId)
                .orElseThrow(()
                        -> new CategoryNotFoundException("Group with this id not exist!!!"));

        user.addGroup(group);
        userRepository.save(user);

        userCache.remove(userId);
        getOne(userId);
    }

    public void updateUser(Long id, User updatedUser) throws UserNotFoundException {
        if (updatedUser.getUsername() == null || updatedUser.getUsername().trim().isEmpty()) {
            throw new ValidationException("Username must not be empty");
        }

        if (updatedUser.getPassword() == null || updatedUser.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password must not be empty");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with this ID not exist!!!"));

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setPassword(updatedUser.getPassword());
        userRepository.save(existingUser);

        userCache.remove(id);
        getOne(id);
    }

    public void delete(Long id) throws UserNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with this id does not exist!!!");
        }
        userRepository.deleteById(id);
        userCache.remove(id);
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
}
