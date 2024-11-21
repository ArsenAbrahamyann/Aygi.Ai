package com.example.demo.dto;

import com.example.demo.payload.request.Location;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class DiaryDTO {
    private int id;
    private String name;
    private String about;
    private boolean isPublic;
    private byte [] diaryPhotoBase64;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    private List<Integer> plannedWorkIds;
    private List<Integer> postIds;
    private Integer userId;

}
