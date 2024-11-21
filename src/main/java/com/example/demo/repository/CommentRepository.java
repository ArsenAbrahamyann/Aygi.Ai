package com.example.demo.repository;

import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByPost_Id(Integer postId);

    @Modifying
    @Query("UPDATE Comment p SET p.deleted = :deleted WHERE p.id = :postId")
    int setDeletedStatusById(Integer postId, boolean deleted);

    List<Comment> findByPostId(Integer postId);
    Comment findCommentById(Integer commentId);

}
