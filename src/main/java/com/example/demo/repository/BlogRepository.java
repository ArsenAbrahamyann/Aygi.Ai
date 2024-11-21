package com.example.demo.repository;

import com.example.demo.entity.Blog;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog,Integer> {
    List<Blog> findByCategory_Name(String categoryName);
    @Override
    Blog save(Blog s);

    @Override
    Optional<Blog> findById(Integer integer);

    @Query(value = "SELECT bl FROM Blog AS bl JOIN FETCH bl.blogPhoto WHERE bl.id = ?1")
    Optional<Blog> findBlobWithPhotoById(Integer id);

    Optional<Blog> findByTitle(String title);

    @Override
    boolean existsById(Integer integer);
    List<Blog> findAllByUser(User user);

    @Transactional
    @Modifying
    @Query("UPDATE Blog d SET d.title = :newTitle, d.text = :newText  WHERE d.id = :id")
    void updateBlogById(Integer id, String newTitle, String newText);

    @Transactional
    @Modifying
    @Query("UPDATE Blog d SET d.title = :newTitle WHERE d.id = :id")
    void updateTitleNameById(Integer id, String newTitle);

    @Transactional
    @Modifying
    @Query("UPDATE Blog d SET d.text = :newText  WHERE d.id = :id")
    void updateBlogTextById(Integer id, String newText);

}
