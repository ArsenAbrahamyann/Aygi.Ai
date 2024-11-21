package com.example.demo.services;

import com.example.demo.entity.BlogPhoto;
import com.example.demo.entity.PostPhoto;
import com.example.demo.exceptions.errors.BadRequestException;
import com.example.demo.repository.PostPhotoRepository;
import com.example.demo.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class PostPhotoService {
    private final PostPhotoRepository postPhotoRepository;
    private final PostService postService;

    public byte[] downloadImage(Integer id){
        Optional<PostPhoto> dbImageData = postPhotoRepository.findById(id);
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
        Optional<PostPhoto> postPhotoOptional = postPhotoRepository.findById(id);
        if (postPhotoOptional.isPresent()) {
            try {
                postPhotoRepository.deleteById(id);
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

