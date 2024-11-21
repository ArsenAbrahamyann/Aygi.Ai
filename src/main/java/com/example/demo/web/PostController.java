package com.example.demo.web;


import com.example.demo.dto.BlogDTO;
import com.example.demo.dto.DiaryDTO;
import com.example.demo.dto.PostDTO;
import com.example.demo.dto.PostMapper;
import com.example.demo.dto.UserProfilePhotoDTO;
import com.example.demo.entity.ActiveWorks;
import com.example.demo.entity.Diary;
import com.example.demo.entity.Post;
import com.example.demo.entity.PostPhoto;
import com.example.demo.payload.request.NewPost;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.repository.ActiveWorksRepository;
import com.example.demo.repository.DiaryRepository;
import com.example.demo.services.ActiveWorksService;
import com.example.demo.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/post")
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Posts", description = "Endpoints related to managing posts")
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;
    private final ActiveWorksService activeWorksService;

    @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(
            summary = "Create a new post",
            description = "This endpoint allows users to create a new post with an optional photo.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Post created successfully",
                            content = @Content(schema = @Schema(implementation = NewPost.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request - validation error",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Validation error",
                                                    value = "{\n" +
                                                            "  \"status\": \"error\",\n" +
                                                            "  \"message\": \"Validation errors occurred. See details for specific fields.\"\n" +
                                                            "}"
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
                                                    name = "Internal server error",
                                                    value = "{\n" +
                                                            "  \"status\": \"error\",\n" +
                                                            "  \"message\": \"Internal server error\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<Object> createPost(
            @RequestParam(name = "About",required = false) String about,
            @RequestParam(name = "DiaryId") Integer diaryId,
            @RequestParam(name = "ActiveWorksList", required = false) List<String> activeWorksListNames,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            Principal principal
    ) {
        if (photo == null) {
            //what to do?

        }
        try {
            if(activeWorksListNames != null) {
                List<ActiveWorks> activeWorksList = activeWorksListNames.stream()
                        .map(name -> {
                            ActiveWorks activeWork = new ActiveWorks();
                            activeWork.setName(name);
                            return activeWork;
                        })
                        .collect(Collectors.toList());
                for (ActiveWorks activeWork : activeWorksList) {
                    activeWorksService.save(activeWork);
                }
                NewPost newPost = new NewPost(about,diaryId, activeWorksList);
                if (photo == null) {
                    photo = new MultipartFile() {
                        @Override
                        public String getName() {
                            return null;
                        }

                        @Override
                        public String getOriginalFilename() {
                            return null;
                        }

                        @Override
                        public String getContentType() {
                            return null;
                        }

                        @Override
                        public boolean isEmpty() {
                            return false;
                        }

                        @Override
                        public long getSize() {
                            return 0;
                        }

                        @Override
                        public byte[] getBytes() throws IOException {
                            return new byte[0];
                        }

                        @Override
                        public InputStream getInputStream() throws IOException {
                            return null;
                        }

                        @Override
                        public void transferTo(File dest) throws IOException, IllegalStateException {

                        }
                    };
                }
                Post post = postService.createPost(photo, newPost,principal);
                PostDTO postDTO = postMapper.toPostDTO(post);
                log.info("Saving Post : {}", postDTO);
                return ResponseEntity.ok(postDTO);
            }else {
                List<ActiveWorks> activeWorksList = new ArrayList<>();
                NewPost newPost = new NewPost(about,diaryId, activeWorksList);
                Post post = postService.createPost(photo, newPost,principal);
                PostDTO postDTO = postMapper.toPostDTO(post);
                log.info("Saving Post : {}", postDTO);
                return ResponseEntity.ok(postDTO);
            }

        } catch (Exception e) {
            log.error("Error creating post: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error creating post: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/updatePrivacy")
    @Operation(
            description = "Endpoint for updating privacy of post entry by ID",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Post entry updated successfully",
                            content = @Content(
                                    schema = @Schema(implementation = PostDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post entry not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                            {
                                "status" : "error",
                                "message" : "Post entry not found"
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
    public ResponseEntity<?> updatePostPrivacy(@PathVariable Integer id,
                                                @RequestParam boolean isPublic)
    {

        PostDTO update = postService.updateDiaryPrivacy(id, isPublic);
        return ResponseEntity.ok(update);
    }

    @PatchMapping(value = "/{id}/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(
            summary = "Update post",
            description = "Endpoint for updating the post",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Post updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PostDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "status" : "error",
                                                        "message" : "Post not found"
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
    public ResponseEntity<?> updatePost(@PathVariable Integer id,
                                        @RequestParam(name = "About",required = false) String about,
                                        @RequestParam(name = "ActiveWorksList", required = false) List<String> activeWorksListNames,
                                        @RequestPart(value = "photo", required = false) MultipartFile photo,
                                         Principal principal) throws IOException {
        PostDTO update = postService.updatePost(id, about,  activeWorksListNames,photo, principal);
        return ResponseEntity.ok(update);
    }

    @GetMapping("/all")
    @Operation(
            summary = "Get all posts",
            description = "This endpoint retrieves a list of all posts",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of posts retrieved successfully",
                            content = @Content(
                                    schema = @Schema(
                                            type = "array",
                                            implementation = PostDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Internal server error",
                                                    value = "{\n" +
                                                            "  \"status\": \"error\",\n" +
                                                            "  \"message\": \"Internal server error\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<List<PostDTO>> getAllPosts( ) {
        List<PostDTO> postDTOList = postService.getAllPosts();
        log.info("Posts retrieved: {}", postDTOList.size());
        return ResponseEntity.ok(postDTOList);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get post by ID",
            description = "Endpoint for retrieving a post by its ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Post found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PostDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "status" : "error",
                                                        "message" : "Post not found"
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
    public ResponseEntity<?> getPostById(@PathVariable Integer id) {
        PostDTO byId = postService.getPostDTOWithPhoto(id);
        return ResponseEntity.ok(byId);
    }

    @GetMapping("posts/{diaryId}")
    @Operation(
            summary = "Get post by DiaryID",
            description = "Endpoint for retrieving a posts by its DiaryID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Post found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PostDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "status" : "error",
                                                        "message" : "Post not found"
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
    public ResponseEntity<?> getPostsByDiaryId(@PathVariable Integer diaryId) {
        List<PostDTO> postsByDiaryId = postService.getPostsByDiaryId(diaryId);
        return ResponseEntity.ok(postsByDiaryId);
    }

    @PostMapping("/{postId}/{userId}/like")
    @Operation(
            summary = "Like a post",
            description = "This endpoint allows a user to like a post.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponse(
            responseCode = "200",
            description = "Post liked successfully",
            content = @Content(
                    schema = @Schema(
                            implementation = PostDTO.class
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Post not found",
            content = @Content(
                    schema = @Schema(implementation = ResponseEntity.class),
                    examples = {
                            @ExampleObject(
                                    name = "Post not found",
                                    value = "{\n" +
                                            "  \"status\": \"error\",\n" +
                                            "  \"message\": \"Post not found\"\n" +
                                            "}"
                            )
                    }
            )
    )
    public ResponseEntity<PostDTO> likePost(@PathVariable("postId") @NonNull Integer postId,
                                            @PathVariable("userId") @NonNull Integer  userId) {
        Post post = postService.likePost(postId, userId);
        PostDTO postDTO = postMapper.toPostDTO(post);

        log.info("getAllPostsForUser is likePost ");
        return new ResponseEntity<>(postDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{postId}")
    @Operation(
            summary = "Delete a post",
            description = "This endpoint allows a user to delete a post.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponse(
            responseCode = "200",
            description = "Post deleted successfully",
            content = @Content(
                    schema = @Schema(
                            implementation = MessageResponse.class
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Post not found",
            content = @Content(
                    schema = @Schema(implementation = ResponseEntity.class),
                    examples = {
                            @ExampleObject(
                                    name = "Post not found",
                                    value = "{\n" +
                                            "  \"status\": \"error\",\n" +
                                            "  \"message\": \"Post not found\"\n" +
                                            "}"
                            )
                    }
            )
    )
    public ResponseEntity<MessageResponse> deletePost(@PathVariable("postId") Integer postId) {
        postService.deletePost(postId);
        log.info("deletePost is successfully ");
        return new ResponseEntity<>(new MessageResponse("Post was deleted"), HttpStatus.OK);
    }

    @GetMapping("/image/{postId}")
    @Operation(
            description = "Endpoint for retrieving a post photo by ID",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User profile photo found",
                            content = @Content(
                                    schema = @Schema(implementation = UserProfilePhotoDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User profile photo not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """ 
                                                            {
                                                                "status" : "error",
                                                                "message" : "User profile photo not found"
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
    public ResponseEntity<?> getPhotoById(@PathVariable Integer postId) {
        byte[] imageData = postService.downloadImage(postId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }
    @GetMapping("/latest")
    @Operation(
            summary = "Get the latest post",
            description = "This endpoint retrieves the most recent post across all users.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Latest post retrieved successfully",
                            content = @Content(
                                    schema = @Schema(implementation = PostDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No posts available",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "No posts available",
                                                    value = "{\n" +
                                                            "  \"status\": \"error\",\n" +
                                                            "  \"message\": \"No posts available\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<PostDTO> getLatestPost() {
        PostDTO postDTO = postService.getLatestPost();
        log.info("Retrieved latest post: {}", postDTO);
        return ResponseEntity.ok(postDTO);
    }
}
