package com.example.demo.dto;

import com.example.demo.dto.DiaryDTO;
import com.example.demo.entity.Diary;
import com.example.demo.entity.DiaryPhoto;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DiaryMapper {

    public DiaryDTO toDiaryDTO(Diary diary) {
        DiaryDTO diaryDTO = new DiaryDTO();
        diaryDTO.setId(diary.getId());
        diaryDTO.setName(diary.getName());
        diaryDTO.setAbout(diary.getAbout());
        diaryDTO.setPublic(diary.isPublic());
        diaryDTO.setCreatedDate(diary.getCreatedDate());

        DiaryPhoto diaryPhoto = diary.getDiaryPhoto();
        if (diaryPhoto != null && diaryPhoto.getImageData() != null) {
            String base64Image = Base64.getEncoder().encodeToString(diaryPhoto.getImageData());
            diaryDTO.setDiaryPhotoBase64(base64Image.getBytes());
        }

        // Map plannedWorkIds
        if (diary.getPlannedWorks() != null) {
            List<Integer> plannedWorkIds = diary.getPlannedWorks().stream()
                    .map(plannedWork -> plannedWork.getId())
                    .collect(Collectors.toList());
            diaryDTO.setPlannedWorkIds(plannedWorkIds);
        }

        // Map postIds
        if (diary.getPosts() != null) {
            List<Integer> postIds = diary.getPosts().stream()
                    .map(post -> post.getId())
                    .collect(Collectors.toList());
            diaryDTO.setPostIds(postIds);
        }

        // Set userId
        if (diary.getUser() != null) {
            diaryDTO.setUserId(diary.getUser().getId());
        }

        return diaryDTO;
    }

    public List<DiaryDTO> toDiaryDTOs(List<Diary> diaries) {
        return diaries.stream().map(this::toDiaryDTO).collect(Collectors.toList());
    }
}