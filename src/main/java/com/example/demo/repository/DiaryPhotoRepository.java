package com.example.demo.repository;

import com.example.demo.entity.DiaryPhoto;
import com.example.demo.entity.UserProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;


public interface DiaryPhotoRepository extends JpaRepository<DiaryPhoto, Integer>
{
    Optional<DiaryPhoto> findByName(String fileName);
    Optional<DiaryPhoto> findById(Integer id);

    @Modifying
    @Transactional
    @Query("UPDATE DiaryPhoto dp SET dp.imageData = :imageData WHERE dp.id = :id")
    void updateDiaryImageDataById(Integer id, byte[] imageData);
}
