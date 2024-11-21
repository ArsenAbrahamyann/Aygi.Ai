package com.example.demo.web;

import com.example.demo.dto.BlogDTO;
import com.example.demo.payload.request.NewBlog;
import com.example.demo.services.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/blog")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Blog", description = "Blog Description")
public class BlogController {
    private final BlogService blogService;
    private final ModelMapper mapper;



    @GetMapping
    @Operation(
            summary = "Get all blogs by user",
            description = "Endpoint for retrieving all blogs belonging to the current user",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Blogs found",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = BlogDTO.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Blogs not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "status" : "error",
                                                        "message" : "Blogs not found"
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
    public ResponseEntity<?> getAllBlogsByUser(Principal principal) {
        List<BlogDTO> allBlogsByUser = blogService.getAllBlogsByUser(principal);
        return ResponseEntity.ok(allBlogsByUser);
    }



    @GetMapping("/{id}")
    @Operation(
            summary = "Get blog by ID",
            description = "Endpoint for retrieving a blog by its ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Blog found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BlogDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Blog not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "status" : "error",
                                                        "message" : "Blog not found"
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
    public ResponseEntity<?> getBlogById(@PathVariable int id) {
        BlogDTO byId = blogService.getBlogDTOWithPhoto(id);
        return ResponseEntity.ok(byId);
    }



    @PatchMapping(value = "/{id}/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(
            summary = "Update blog",
            description = "Endpoint for updating the title, text, and/or categories of a blog",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Blog updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BlogDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Blog not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "status" : "error",
                                                        "message" : "Blog not found"
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
    public ResponseEntity<?> updateDiary(@PathVariable int id,
                                         @RequestPart(value = "title", required = false) String title,
                                         @RequestPart(value = "text", required = false) String text,
                                         @RequestPart(value = "category", required = false) String category,
                                         Principal principal) {
        BlogDTO update = blogService.updateBlog(id, title, text, category, principal);
        return ResponseEntity.ok(update);
    }

    @PostMapping(value = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(
            summary = "Create a new blog",
            description = "Endpoint for creating a new blog with a photo, title, text, and categories",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Blog created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BlogDTO.class)
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
    public ResponseEntity<Object> create(
            @RequestPart(name = "photo") MultipartFile photo,
            @RequestParam(name = "title") String title,
            @RequestParam(name = "text") String text,
            @RequestParam(name = "category") String category,
            Principal principal) {
        NewBlog newBlog = new NewBlog(title, text);
        BlogDTO blogDTO = blogService.createBlog(newBlog, photo, category, principal);
        return new ResponseEntity<>(blogDTO, HttpStatus.CREATED);
    }



    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a blog",
            description = "Endpoint for deleting a blog by its ID",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Blog deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Blog not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "status" : "error",
                                                        "message" : "Blog not found"
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
    public ResponseEntity<?> delete(@PathVariable int id) {
        blogService.delete(id);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/image/{blogId}")
    @Operation(
            summary = "Delete a blog image by ID",
            description = "Endpoint for deleting a blog image by ID",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Blog image successfully deleted"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Blog image not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "status" : "error",
                                                        "message" : "Blog image not found"
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
    public ResponseEntity<?> deleteImage(@PathVariable int blogId) {
        blogService.deleteImage(blogId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/image/{blogId}")
    @Operation(
            summary = "Get photo by blog ID",
            description = "Endpoint for retrieving a blog image by blog ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Blog image retrieved successfully",
                            content = @Content(
                                    mediaType = "image/png"
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Blog image not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "status" : "error",
                                                        "message" : "Blog image not found"
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
    public ResponseEntity<?> getPhotoById(@PathVariable int blogId) {
        byte[] imageData = blogService.downloadImage(blogId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }


    @PatchMapping(value = "/image/{blogId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(
            summary = "Update blog image",
            description = "Endpoint for updating a blog image",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Blog image updated successfully",
                            content = @Content(
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Blog image not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "status" : "error",
                                                        "message" : "Blog image not found"
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
    public ResponseEntity<?> updateImage(@PathVariable int blogId, @RequestPart MultipartFile photo) {
        String updateBlogPhoto = blogService.updateImage(blogId, photo);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updateBlogPhoto);
    }
}
