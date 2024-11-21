package com.example.demo.services;

import com.example.demo.entity.DiaryPhoto;
import com.example.demo.entity.User;
import com.example.demo.entity.UserProfilePhoto;
import com.example.demo.exceptions.errors.BadRequestException;
import com.example.demo.repository.DiaryPhotoRepository;
import com.example.demo.repository.UserProfilePhotoRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryPhotoService
{

    private final DiaryPhotoRepository diaryPhotoRepository;
    private final DiaryService diaryService;

    public byte[] downloadImage(Integer id){
        Optional<DiaryPhoto> dbImageData = diaryPhotoRepository.findById(id);
        if(dbImageData.isPresent())
        {
            byte[] image = ImageUtils.decompressImage(dbImageData.get().getImageData());
            return image;
        }
        else {
            log.warn("Image with ID {} not found", id);
            throw new BadRequestException("Image not found for the given ID");
        }


    }

    public void deleteImage(Integer id) {
        Optional<DiaryPhoto> diaryPhotoOptional = diaryPhotoRepository.findById(id);
        if (diaryPhotoOptional.isPresent()) {
            try {
                diaryPhotoRepository.deleteById(id);
                log.info("Image with ID {} deleted successfully", id);
            } catch (Exception e) {
                log.error("Error occurred while deleting image with ID {}: {}", id, e.getMessage());
                throw new RuntimeException("Failed to delete image");
            }
        } else {
            log.warn("Image with ID {} not found", id);
            throw new BadRequestException("Image not found for the given ID");
        }
    }
}
