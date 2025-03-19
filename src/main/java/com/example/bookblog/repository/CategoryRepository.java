package com.example.bookblog.repository;

import com.example.bookblog.entity.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT * FROM categories c WHERE (SELECT COUNT(*) "
            + "FROM user_categories uc WHERE uc.category_id = c.id) >= :minUsers",
            nativeQuery = true)
    List<Category> findCategoriesByMinUsersNative(@Param("minUsers") int minUsers);


    @Query("SELECT c FROM Category c WHERE SIZE(c.users) >= :minUsers")
    List<Category> findCategoriesByMinUsers(@Param("minUsers") int minUsers);


    @Query("SELECT c FROM Category c JOIN c.users u "
            + "WHERE (:name IS NULL OR c.name LIKE %:name%) "
            + "AND (:username IS NULL OR u.username LIKE %:username%)")
    List<Category> searchCategories(@Param("name") String name, @Param("username") String username);

    Category findByName(String name);

    @EntityGraph(attributePaths = "users")
    Optional<Category> findWithUsersById(Long id);
}

