package com.example.demo.services;

import com.example.demo.dto.CommentDTO;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class CommentService {
    public static final Logger LOG = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final ModelMapper modelMapper;
    @Lazy
    private final PostService postService;

    public List<CommentDTO> getAllCommentsForPost(Integer postId) {
        Post postById = postService.getPostById(postId);
        List<Comment> comments = postById.getComments();
        List<CommentDTO> commentDTOS = new ArrayList<>();
        for (Comment comment : comments) {
            User userById = userService.getUserById(comment.senderUserId());
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setMessage(comment.message());
            commentDTO.setCommentId(comment.id());
            commentDTO.setLikes(comment.likes());
            commentDTO.setPostId(comment.post().getId());
            commentDTO.setUsername(userById.getUsername());
            commentDTO.setUserId(userById.getId());

            commentDTOS.add(commentDTO);
        }

        return commentDTOS;
    }

    public CommentDTO toDoCommentDto(Comment comment) {
        User userById = userService.getUserById(comment.senderUserId());
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setUserId(comment.senderUserId());
        commentDTO.setCommentId(comment.id());
        commentDTO.setLikes(comment.likes());
        commentDTO.setUsername(userById.getUsername());
        commentDTO.setMessage(comment.message());
        commentDTO.setPostId(comment.post().getId());
        return commentDTO;
    }

    public void deleteComment(int commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        comment.ifPresent(commentRepository::delete);
    }

    public CommentDTO saveComment( CommentDTO commentDTO, Principal principal) {
        User userByPrincipal = userService.getUserByPrincipal(principal);
//        String s = imageService.uploadImageToComment(photo);
        Comment comment = modelMapper.map(commentDTO, Comment.class);
        comment
//                .photoName(s)
                .post(postService.getPostById(commentDTO.getPostId()))
                .message(commentDTO.getMessage())
                .senderUserId(userByPrincipal.getId());
        comment = commentRepository.save(comment);
        CommentDTO commentDTO1 = new CommentDTO();
        commentDTO1.setPostId(comment.post().getId());
        commentDTO1.setCommentId(comment.id());
        commentDTO1.setMessage(comment.message());
        commentDTO1.setLikes(comment.likes());
        commentDTO1.setUsername(userByPrincipal.getUsername());
        commentDTO1.setUserId(userByPrincipal.getId());
        LOG.info("Saving comment for Post: {}", commentDTO.getPostId());
        return commentDTO1;
    }

    public void makeToDeleteComment(int commentId, int postId) {
        commentRepository.deleteById(commentId);
            commentRepository.setDeletedStatusById(postId, true);
    }

    public List<Integer> getAllCommentIdsByPostId(Integer postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        List<Integer> ids = new ArrayList<>();
        for (Comment comment : comments) {
            ids.add(comment.id());
        }
        return ids;

    }

    public CommentDTO likePost(Integer postId, Integer commentId, Principal principal) {
        User userByPrincipal = userService.getUserByPrincipal(principal);
        Post postById = postService.getPostById(postId);
        if (postById == null) {
            throw new EntityNotFoundException("Post not found");
        }

        Optional<Comment> optionalComment = commentRepository.findById(commentId);

        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            comment.likes(comment.likes() != null ? comment.likes() + 1 : 1);
            commentRepository.save(comment);
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setUsername(userByPrincipal.getUsername());
            commentDTO.setCommentId(comment.id());
            commentDTO.setLikes(comment.likes());
            commentDTO.setMessage(comment.message());
            commentDTO.setPostId(comment.post().getId());
            commentDTO.setUserId(userByPrincipal.getId());
            return commentDTO;
        } else {
            throw new EntityNotFoundException("Comment not found");
        }
    }
}
