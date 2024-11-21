package com.example.demo.dto;

import com.example.demo.entity.ActiveWorks;
import com.example.demo.entity.Blog;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.services.CommentService;
import com.example.demo.services.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostMapper {
    private final UserService userService;
    public  PostDTO toPostDTO(Post post) {

        List<CommentDTO> commentDTOS = new ArrayList<>();
        List<Comment> comments = post.getComments();
        if (comments != null) {
            for (Comment comment : comments) {
                User userById = userService.getUserById(comment.senderUserId());
                CommentDTO commentDTO = new CommentDTO();
                commentDTO.setUserId(comment.senderUserId());
                commentDTO.setCommentId(comment.id());
                commentDTO.setLikes(comment.likes());
                commentDTO.setUsername(userById.getUsername());
                commentDTO.setMessage(comment.message());
                commentDTO.setPostId(comment.post().getId());
                commentDTOS.add(commentDTO);
            }
        }

        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setAbout(post.getAbout());
        postDTO.setUserId(post.getUser().getId());
        postDTO.setDiaryId(post.getDiary().getId());
        postDTO.setComments(commentDTOS);
        postDTO.setPublic(post.isPublic());
        postDTO.setLikes(post.getLikes());
        postDTO.setCreatedDate(post.getCreatedDate());
        postDTO.setActiveWorksList(post.getActiveWorks().stream().map(ActiveWorks::getName).collect(Collectors.toList()));

        if (post.getPostPhoto() != null) {
            postDTO.setImage(post.getPostPhoto().getImageData());
        }

        return postDTO;
    }

    public List<PostDTO> toPostDTOs(List<Post> posts) {
        return posts.stream()
                .map(this::toPostDTO)
                .collect(Collectors.toList());
    }
}
