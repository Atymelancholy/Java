package com.example.bookblog.repository;

import com.example.bookblog.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);

    @EntityGraph(attributePaths = "users")
    Optional<Category> findWithUsersById(Long id);
}
