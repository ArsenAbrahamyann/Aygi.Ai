package com.example.demo.dto;

import com.example.demo.entity.Diary;
import com.example.demo.entity.enums.ERole;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class UserDTO {

    private int id;
    @NotEmpty
    private String username;
    @NotEmpty
    private String email;
    private String bio;
    private boolean active;
    private String activationCode;
    private Set<ERole> roles;
    private String profilePhotoBase64;
    private List<DiaryDTO> diaries;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
}
