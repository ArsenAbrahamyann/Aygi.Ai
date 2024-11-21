package com.example.demo.repository;

import com.example.demo.entity.Diary;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Optional<Post> findByIdAndDeletedIsFalse(int postId);
    List<Post> findAllByUser(User user);
    List<Post> findAll();
//    void updatePost(Post post);

    List<Post> findAllByDeletedFalseOrderByCreatedDateDesc();

    Optional<Post> findPostByIdAndDiary(int postId, Diary diary);

    List<Post> findAllByDiaryInAndDeletedFalseOrderByCreatedDateDesc(List<Diary> diaries);

    @Query(value = "SELECT post.* FROM post " +
            "JOIN diary ON post.diary_id = diary.id " +
            "JOIN user ON diary.user_id = user.id " +
            "WHERE user.id = :userId and post.deleted = false", nativeQuery = true)
    List<Post> findAllPostsByUserId(@Param("userId") Integer userId);

    @Query(value = "SELECT post.* FROM post " +
            "JOIN diary ON post.diary_id = diary.id " +
            "JOIN user ON diary.user_id = user.id " +
            "WHERE user.id = :userId and post.id = :postId and post.deleted = false", nativeQuery = true)
    List<Post> findAllPostsByUserIdAndPostId(@Param("userId") Integer userId, @Param("postId") Integer postId);

    @Modifying
    @Query("UPDATE Post p SET p.deleted = :deleted WHERE p.id = :postId")
    Integer setDeletedStatusById(Integer postId, boolean deleted);
    Optional<Post> findTopByOrderByCreatedDateDesc();

    List<Post> findAllPostsByDiaryId(Integer diaryId);

}
