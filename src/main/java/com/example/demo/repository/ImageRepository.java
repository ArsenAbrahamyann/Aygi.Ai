package com.example.demo.repository;

import com.example.demo.entity.PostPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<PostPhoto, Long> {

    Optional<PostPhoto> findByUserId(Long userId);

    Optional<PostPhoto> findByPostId(Long postId);

}
