package com.example.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class DiaryPhotoDTO {
    private Integer diaryId;
    private List<byte[]> photos;
}
