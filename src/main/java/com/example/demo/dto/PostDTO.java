package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Integer id;
    private String about;
    private Integer diaryId;
    private Integer userId;

    private Integer likes = 0;
    private LocalDateTime createdDate;
    private List<Integer> likedUsers;
    private List<String> LikedUsernames;
    private List<CommentDTO> comments;
    private List<String> activeWorksList;
    private byte[] image;

    private boolean isPublic;

}
