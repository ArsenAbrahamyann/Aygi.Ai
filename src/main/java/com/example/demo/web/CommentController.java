package com.example.demo.web;

import com.example.demo.dto.CommentDTO;
import com.example.demo.entity.Comment;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.services.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/comment")
@CrossOrigin
@Tag(name = "Comment", description = "Endpoints for managing comments")
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final ModelMapper mapper;

    @PostMapping("/create")
    @Operation(
            summary = "Create a new comment",
            description = "Endpoint for creating a new comment with message and postId",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Comment created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommentDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                    {
                        "status" : "error",
                        "message" : "Bad request"
                    }
                    """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                    {
                        "status" : "error",
                        "message" : "Internal server error"
                    }
                    """
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<Object> createComment(
            @RequestParam("postId") Integer postId,
            @RequestParam("message") String message,
            Principal principal) {

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setPostId(postId);
        commentDTO.setMessage(message);

        CommentDTO commentDTO1 = commentService.saveComment(commentDTO, principal);
        return new ResponseEntity<>(commentDTO1, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}/all")
    @Operation(
            description = "Endpoint to retrieve all comments for a post",
            security = {@SecurityRequirement(name = "bearerAuth")},

            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comments successfully retrieved",
                            content = @Content(
                                    schema = @Schema(implementation = CommentDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                            [
                                {
                                    "id": 1,
                                    "postId": 123,
                                    "text": "This is a comment",
                                    "user": {
                                        "id": 456,
                                        "username": "example_user"
                                    }
                                },
                                {
                                    "id": 2,
                                    "postId": 123,
                                    "text": "Another comment",
                                    "user": {
                                        "id": 789,
                                        "username": "another_user"
                                    }
                                }
                            ]
                            """
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<List<CommentDTO>> getAllCommentsFromPost(@PathVariable("postId") Integer postId) {
        List<CommentDTO> commentDTOList = commentService.getAllCommentsForPost(postId);
        return new ResponseEntity<>(commentDTOList, HttpStatus.OK);
    }

    @GetMapping("/{postId}/comments/ids")
    @Operation(
            summary = "Get all comment IDs for a post",
            description = "Endpoint for retrieving all comment IDs associated with a specific post",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved comment IDs",
                            content = @Content(
                                    schema = @Schema(implementation = List.class),
                                    examples = @ExampleObject(
                                            name = "Success",
                                            value = """
                    [1, 2, 3, 4]
                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found",
                            content = @Content(
                                    schema = @Schema(implementation = MessageResponse.class),
                                    examples = @ExampleObject(
                                            name = "Not Found",
                                            value = """
                    {
                      "status": "error",
                      "message": "Post not found"
                    }
                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    schema = @Schema(implementation = MessageResponse.class),
                                    examples = @ExampleObject(
                                            name = "Error",
                                            value = """
                    {
                      "status": "error",
                      "message": "Internal server error"
                    }
                    """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<List<Integer>> getAllCommentIds(@PathVariable("postId") Integer postId) {
        List<Integer> commentIds = commentService.getAllCommentIdsByPostId(postId);
        return new ResponseEntity<>(commentIds, HttpStatus.OK);
    }

    @DeleteMapping("/{postId}/{commentId}/delete")
    @Operation(
            summary = "Delete a comment",
            description = "Endpoint for deleting a comment by its ID and the associated post ID",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comment successfully deleted",
                            content = @Content(
                                    schema = @Schema(implementation = MessageResponse.class),
                                    examples = @ExampleObject(
                                            name = "Success",
                                            value = """
                    {
                      "status": "success",
                      "message": "Comment was deleted"
                    }
                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment or Post not found",
                            content = @Content(
                                    schema = @Schema(implementation = MessageResponse.class),
                                    examples = @ExampleObject(
                                            name = "Not Found",
                                            value = """
                    {
                      "status": "error",
                      "message": "Comment or Post not found"
                    }
                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    schema = @Schema(implementation = MessageResponse.class),
                                    examples = @ExampleObject(
                                            name = "Error",
                                            value = """
                    {
                      "status": "error",
                      "message": "Internal server error"
                    }
                    """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable("commentId") Integer commentId,
                                                         @PathVariable("postId") Integer postId) {
        commentService.makeToDeleteComment(commentId, postId);
        return new ResponseEntity<>(new MessageResponse("Comment was deleted"), HttpStatus.OK);
    }

    @PostMapping("/{postId}/{commentId}/like")
    @Operation(
            summary = "Like a comment",
            description = "This endpoint allows a user to like a comment.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponse(
            responseCode = "200",
            description = "Comment liked successfully",
            content = @Content(
                    schema = @Schema(
                            implementation = CommentDTO.class
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Comment not found",
            content = @Content(
                    schema = @Schema(implementation = ResponseEntity.class),
                    examples = {
                            @ExampleObject(
                                    name = "Comment not found",
                                    value = "{\n" +
                                            "  \"status\": \"error\",\n" +
                                            "  \"message\": \"Comment not found\"\n" +
                                            "}"
                            )
                    }
            )
    )
    public ResponseEntity<CommentDTO> likeComment(@PathVariable("postId") @NonNull Integer postId,
                                            @PathVariable("commentId") @NonNull Integer  commentId,Principal principal) {
        CommentDTO commentDTO = commentService.likePost(postId, commentId,principal);

        log.info("getAllPostsForUser is likePost ");
        return new ResponseEntity<>(commentDTO, HttpStatus.OK);
    }

}
