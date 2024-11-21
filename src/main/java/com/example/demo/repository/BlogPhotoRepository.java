package com.example.demo.repository;

import com.example.demo.entity.BlogPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Optional;

public interface BlogPhotoRepository extends JpaRepository<BlogPhoto,Integer> {
    Optional<BlogPhoto> findByName(String fileName);
    Optional<BlogPhoto> findById(Integer id);

    @Modifying
    @Transactional
    @Query("UPDATE BlogPhoto dp SET dp.imageData = :imageData WHERE dp.id = :id")
    void updateBlogImageDataById(Integer id, byte[] imageData);
}
