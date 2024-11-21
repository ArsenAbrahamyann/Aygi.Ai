package com.example.demo.dto;

import lombok.Data;

@Data
public class CommentDTO {
    private Integer postId;
    private String message;
    private Integer likes = 0;
    private String username;
    private Integer userId;
    private Integer commentId;
}
