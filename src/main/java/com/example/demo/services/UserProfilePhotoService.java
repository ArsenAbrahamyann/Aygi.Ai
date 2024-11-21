package com.example.demo.services;


//import com.example.demo.dto.UserProfilePhotoDTO;
//import com.example.demo.entity.User;
//import com.example.demo.entity.UserProfilePhoto;
//import com.example.demo.exceptions.errors.BadRequestException;
//import com.example.demo.repository.UserProfilePhotoRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.File;
//import java.io.IOException;
//import java.security.Principal;
//import java.util.Optional;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class UserProfilePhotoService
//{
//    @Value("${constants.user.image.path}")
//    private String USER_PROFILE_IMAGE_PATH;
//    private final UserProfilePhotoRepository userProfilePhotoRepository;
//
//    private final UserService userService;
//    private final ModelMapper mapper;
//
//    public UserProfilePhotoDTO addUserProfileImage(MultipartFile photo, Principal principal) {
//        try {
//            User user = userService.getUserByPrincipal(principal);
//            if(user.getProfilePhoto() != null)
//            {
//                throw new BadRequestException();
//            }
//            String photoPath = null;
//            if (!photo.isEmpty())
//            {
//                log.info("Uploading image to user profile");
//
//                photoPath = this.uploadImageToUserProfile(photo);
//            }
//
//            UserProfilePhoto userProfilePhoto = new UserProfilePhoto(photoPath, user);
//            userProfilePhoto = userProfilePhotoRepository.save(userProfilePhoto);
//            log.info("Upload image successfully");
//            return mapper.map(userProfilePhoto, UserProfilePhotoDTO.class);
//        } catch (Exception e) {
//            log.error("Upload image error : {} ", e);
//            throw new BadRequestException(e.getMessage());
//        }
//
//    }
//    public String uploadImageToUserProfile(MultipartFile photo) {
//
//        String photoPath = System.nanoTime() + photo.getOriginalFilename();
//        try {
//            photo.transferTo(new File(USER_PROFILE_IMAGE_PATH + photoPath));
//        } catch (IOException e) {
//            log.error(e.getMessage());
//            return null;
//        }
//        return photoPath;
//    }
//
//
//    public void deleteFile(String path)
//    {
//        File imageFile = new File(USER_PROFILE_IMAGE_PATH + path);
//        if (imageFile.delete()) {
//            log.info("Image file for user deleted successfully.");
//        } else {
//            log.error("Failed to delete image file for user ");
//        }
//    }
//    public void delete(int id)
//    {
//        Optional<UserProfilePhoto> userProfilePhoto = userProfilePhotoRepository.findById(id);
//        if(userProfilePhoto.isPresent())
//        {
//            String path = userProfilePhoto.get().getPhotoPath();
//            userProfilePhotoRepository.deleteById(id);
//            deleteFile(path);
//            log.info("User for id {id} is deleted",id);
//        }
//        else {
//            throw new BadRequestException("There is no profile photo to delete");
//        }
//
//    }
//
//
//    public UserProfilePhotoDTO getById(int id, Principal principal) {
//        Optional<UserProfilePhoto> optionalUserProfilePhoto = getByIdOptional(id);
//        User user = userService.getUserByPrincipal(principal);
//
//        if (optionalUserProfilePhoto.isPresent())
//        {
//            UserProfilePhoto userProfilePhoto = user.getProfilePhoto();
//            return mapper.map(userProfilePhoto, UserProfilePhotoDTO.class);
//        }
//        else {
//            throw new BadRequestException("There is no profile photo to get");
//
//        }
//    }
//
//    public Optional<UserProfilePhoto> getByIdOptional(int id) {
//        return userProfilePhotoRepository.findById(id);
//    }
//
//
//    public UserProfilePhotoDTO update(int id, MultipartFile photo, Principal principal) {
//        User user = userService.getUserByPrincipal(principal);
//
//        try {
//            Optional<UserProfilePhoto> photoOptional = userProfilePhotoRepository.findById(id);
//            if (photoOptional.isPresent())
//            {
//                String oldPhotoPath = photoOptional.get().getPhotoPath();
//                String newPhotoPath = this.uploadImageToUserProfile(photo);
//
//                userProfilePhotoRepository.updatePhotoPathById(id, newPhotoPath);
////                UserProfilePhoto userProfilePhoto = user.getProfilePhoto();
////                userProfilePhoto.setPhotoPath(newPhotoPath);
//                photoOptional.get().setPhotoPath(newPhotoPath);
//                deleteFile(oldPhotoPath);
//                return mapper.map(photoOptional, UserProfilePhotoDTO.class);
//            }
//            else {
//                throw new BadRequestException("There is no profile photo to update");
//            }
//        } catch (Exception e) {
//            log.error("Update image error : {} ", e);
//            throw new BadRequestException(e.getMessage());
//        }
//    }
//
//}


import com.example.demo.entity.User;
import com.example.demo.entity.UserProfilePhoto;
import com.example.demo.exceptions.errors.BadRequestException;
import com.example.demo.repository.UserProfilePhotoRepository;
import com.example.demo.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfilePhotoService
{

    private final UserProfilePhotoRepository userProfilePhotoRepository;
    private final UserService userService;

    public String uploadImage(MultipartFile file, Principal principal) throws IOException
    {

        User user = userService.getUserByPrincipal(principal);
        if (user.getProfilePhoto() != null) {
            return "User already has a profile photo: If need change photo please try to update";

        }
        UserProfilePhoto userProfilePhoto = userProfilePhotoRepository.save(UserProfilePhoto.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes()))
                .user(userService.getUserByPrincipal(principal))
                .build());
        if(userProfilePhoto != null)     //it means it is saved in the db
        {
            return "File uploaded successfully" + file.getOriginalFilename()
                    + "\n ImageId: " + userProfilePhoto.getId()
                    + "\n UserId: " + user.getId();
        }
        return null;
    }


    public byte[] downloadImage(Integer userId){

        User user =  userService.getUserById(userId);
        UserProfilePhoto dbImageData = user.getProfilePhoto();
        if(dbImageData != null)
        {
            byte[] image = ImageUtils.decompressImage(dbImageData.getImageData());
            return image;
        }
        else {
            log.warn("Image with ID {} not found", userId);
            throw new BadRequestException("Image not found for the given ID");
        }


    }

    public void deleteImage(Integer userId) {
//        Optional<UserProfilePhoto> userProfilePhotoOptional = userProfilePhotoRepository.findById(id);

        User user =  userService.getUserById(userId);
        UserProfilePhoto userProfilePhotoOptional = user.getProfilePhoto();
        if (userProfilePhotoOptional != null) {
            try {
                userProfilePhotoRepository.deleteByUserId(userId);
                log.info("Image with ID {} deleted successfully", userId);
            } catch (Exception e) {
                log.error("Error occurred while deleting image with ID {}: {}", userId, e.getMessage());
                throw new RuntimeException("Failed to delete image");
            }
        } else {
            log.warn("Image with ID {} not found", userId);
            throw new BadRequestException("Image not found for the given ID");
        }
    }


    public String updateImage(Integer userId, MultipartFile file)
    {
//        Optional<UserProfilePhoto> userProfilePhotoOptional = userProfilePhotoRepository.findById(id);

        User user =  userService.getUserById(userId);
        UserProfilePhoto userProfilePhoto = user.getProfilePhoto();
        if (userProfilePhoto != null) {
            try {
                // Compress the new image data
                byte[] compressedImageData = ImageUtils.compressImage(file.getBytes());

                // Update the image data in the repository
                userProfilePhotoRepository.updateByUserId(userId, compressedImageData);

                // Fetch the updated entity
//                UserProfilePhoto userProfilePhoto = userProfilePhotoOptional.get();
                userProfilePhoto.setImageData(compressedImageData);

                return "Image updated successfully";
            } catch (IOException e) {
                log.error("Error occurred while updating image with ID {}: {}", userId, e.getMessage());
                throw new RuntimeException("Failed to update image");
            }
        } else {
            log.warn("Image with ID {} not found", userId);
            throw new IllegalArgumentException("Image not found for the given ID");
        }
    }
}