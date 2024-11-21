package com.example.demo.payload.request;

import com.example.demo.entity.ActiveWorks;
import com.example.demo.entity.Post;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewPost {


    @NotEmpty(message = "About is required")
    private String about;

    @NotNull(message = "diary ID is required")
    private Integer diaryId;
    private List<ActiveWorks> activeWorksList;
}
