package com.example.bookblog.service;

import com.example.bookblog.cache.InMemoryCache;
import com.example.bookblog.dto.CategoryWithUsersDto;
import com.example.bookblog.entity.Category;
import com.example.bookblog.entity.User;
import com.example.bookblog.exception.CategoryAlreadyExistException;
import com.example.bookblog.exception.CategoryNotFoundException;
import com.example.bookblog.repository.CategoryRepository;
import com.example.bookblog.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final InMemoryCache<Long, CategoryWithUsersDto> categoryCache;
    private final InMemoryCache<String, List<CategoryWithUsersDto>> searchCache;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository,
                           UserRepository userRepository,
                           InMemoryCache<Long, CategoryWithUsersDto>
                                       categoryCache, InMemoryCache<String,
            List<CategoryWithUsersDto>> searchCache) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.categoryCache = categoryCache;
        this.searchCache = searchCache;
    }

    public List<CategoryWithUsersDto> searchCategories(String name, String username) {
        String cacheKey = "search_"
                + (name != null ? name : "all") + "_" + (username != null ? username : "all");
        logger.info("Searching categories with key: {}", cacheKey);
        return searchCache.getOrCompute(cacheKey, () -> {
            logger.info("Cache miss: fetching categories from database");
            return categoryRepository.searchCategories(name, username)
                    .stream()
                    .map(CategoryWithUsersDto::toModel)
                    .toList();
        });
    }

    public Category registration(Category category) throws CategoryAlreadyExistException {
        logger.info("Registering new category: {}", category.getName());
        if (categoryRepository.findByName(category.getName()) != null) {
            throw new CategoryAlreadyExistException("Category with this name already exists!");
        }
        Category savedCategory = categoryRepository.save(category);
        categoryCache.put(savedCategory.getId(), CategoryWithUsersDto.toModel(savedCategory));
        logger.info("Category registered and cached with ID: {}", savedCategory.getId());
        return savedCategory;
    }

    public CategoryWithUsersDto getOne(Long id) throws CategoryNotFoundException {
        logger.info("Fetching category with ID: {}", id);
        return categoryCache.getOrCompute(id, () -> {
            logger.info("Cache miss: fetching category from database");
            try {
                return categoryRepository.findWithUsersById(id)
                        .map(CategoryWithUsersDto::toModel)
                        .orElseThrow(()
                                -> new CategoryNotFoundException("Category "
                                + "with this id does not exist!"));
            } catch (CategoryNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<CategoryWithUsersDto> findCategoriesByMinUsers(int minUsers) {
        String cacheKey = "minUsers_" + minUsers;
        logger.info("Fetching categories with at least {} users, "
                + "cache key: {}", minUsers, cacheKey);

        return searchCache.getOrCompute(cacheKey, () -> {
            logger.info("Cache miss: fetching categories from database");
            return categoryRepository.findCategoriesByMinUsers(minUsers)
                    .stream()
                    .map(CategoryWithUsersDto::toModel)
                    .toList();
        });
    }

    public List<CategoryWithUsersDto> findCategoriesByMinUsersNative(int minUsers) {
        String cacheKey = "minUsersNative_" + minUsers;
        logger.info("Fetching native categories with at least {} "
                + "users, cache key: {}", minUsers, cacheKey);

        return searchCache.getOrCompute(cacheKey, () -> {
            logger.info("Cache miss: fetching categories from database");
            return categoryRepository.findCategoriesByMinUsersNative(minUsers)
                    .stream()
                    .map(CategoryWithUsersDto::toModel)
                    .toList();
        });
    }

    @Transactional
    public void deleteGroup(Long groupId) throws CategoryNotFoundException {
        logger.info("Deleting category with ID: {}", groupId);
        Category category = categoryRepository.findById(groupId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        Set<User> users = new HashSet<>(category.getUsers());
        for (User user : users) {
            user.removeGroup(category);
            userRepository.save(user);
        }

        categoryRepository.delete(category);
        categoryCache.remove(groupId);
        searchCache.clear();
        logger.info("Category deleted and removed from cache");
    }

    public void updateGroup(Long id, Category updatedCategory) throws CategoryNotFoundException {
        logger.info("Updating category with ID: {}", id);
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(()
                        -> new CategoryNotFoundException("Category with this ID does not exist!"));
        existingCategory.setName(updatedCategory.getName());

        categoryRepository.save(existingCategory);
        categoryCache.put(id, CategoryWithUsersDto.toModel(existingCategory));
        searchCache.clear();
        logger.info("Category updated and cache refreshed");
    }
}
