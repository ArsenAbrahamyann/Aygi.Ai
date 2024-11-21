package com.example.demo.detectionHistory;

import com.example.demo.entity.DiaryPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DetectionPhotoRepository extends JpaRepository<DetectionPhoto, Integer>
{
    Optional<DetectionPhoto> findByName(String fileName);
    Optional<DetectionPhoto> findById(Integer id);
}
