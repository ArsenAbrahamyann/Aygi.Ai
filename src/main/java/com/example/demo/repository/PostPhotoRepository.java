package com.example.demo.repository;

import com.example.demo.entity.BlogPhoto;
import com.example.demo.entity.PostPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

public interface PostPhotoRepository extends JpaRepository<PostPhoto,Integer> {
    Optional<PostPhoto> findByName(String fileName);
    Optional<PostPhoto> findById(Integer id);
    @Query("SELECT pp FROM PostPhoto pp JOIN pp.post p WHERE p.id = :postId")
    Optional<PostPhoto> findByPostId(@Param("postId") Long postId);

    @Modifying
    @Transactional
    @Query("UPDATE PostPhoto dp SET dp.imageData = :imageData WHERE dp.id = :id")
    void updatePostImageDataById(Integer id, byte[] imageData);
}
