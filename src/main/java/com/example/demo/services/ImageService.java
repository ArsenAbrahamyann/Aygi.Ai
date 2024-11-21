package com.example.demo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class ImageService {
    @Value("${constants.diary.image.path}")
    private String DIARY_IMAGE_PATH;
    @Value("${constants.comment.image.path}")
    private String COMMENT_IMAGE_PATH;

    public String uploadImage(MultipartFile photo) {

        String photoPath = System.nanoTime() + photo.getOriginalFilename();
        try {
            photo.transferTo(new File(DIARY_IMAGE_PATH + photoPath));
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
        return photoPath;
    }

    public String uploadImageToComment(MultipartFile photo) {

        String photoPath = System.nanoTime() + photo.getOriginalFilename();
        try {
            photo.transferTo(new File(COMMENT_IMAGE_PATH + photoPath));
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
        return photoPath;
    }

    public void deleteFile(String path)
    {
        File imageFile = new File(DIARY_IMAGE_PATH + path);
        if (imageFile.delete()) {
            log.info("Image file for user deleted successfully.");
        } else {
            log.error("Failed to delete image file for user ");
        }
    }
}
