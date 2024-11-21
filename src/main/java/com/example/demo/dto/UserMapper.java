package com.example.demo.dto;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.entity.UserProfilePhoto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class UserMapper {

    @Autowired
    private DiaryMapper diaryMapper;

    public UserDTO toUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setBio(user.getBio());
        userDTO.setActive(user.isActive());
        userDTO.setActivationCode(user.getActivationCode());
        userDTO.setRoles(user.getRoles());
        userDTO.setCreatedDate(user.getCreatedDate());

        UserProfilePhoto profilePhoto = user.getProfilePhoto();
        if (profilePhoto != null && profilePhoto.getImageData() != null) {
            String base64Image = Base64.getEncoder().encodeToString(profilePhoto.getImageData());
            userDTO.setProfilePhotoBase64(base64Image);
        }

        userDTO.setDiaries(diaryMapper.toDiaryDTOs(user.getDiaries()));

        return userDTO;
    }
}