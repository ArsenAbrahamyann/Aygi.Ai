package com.example.demo.services;

import com.example.demo.entity.BlogPhoto;
import com.example.demo.entity.DiaryPhoto;
import com.example.demo.exceptions.errors.BadRequestException;
import com.example.demo.repository.BlogPhotoRepository;
import com.example.demo.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogPhotoService {
    private final BlogPhotoRepository blogPhotoRepository;
    private final BlogService blogService;

    public byte[] downloadImage(Integer id){
        Optional<BlogPhoto> dbImageData = blogPhotoRepository.findById(id);
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
        Optional<BlogPhoto> blogPhotoOptional = blogPhotoRepository.findById(id);
        if (blogPhotoOptional.isPresent()) {
            try {
                blogPhotoRepository.deleteById(id);
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
