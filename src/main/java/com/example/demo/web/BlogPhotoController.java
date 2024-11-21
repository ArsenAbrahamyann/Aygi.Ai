package com.example.demo.web;

import com.example.demo.dto.UserProfilePhotoDTO;
import com.example.demo.services.BlogPhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("api/blogImages")
@RestController
@Tag(name = "blog Photo", description = "Endpoints for managing blog photos")
public class BlogPhotoController {
    private static final Logger log = LoggerFactory.getLogger(UserProfilePhotoController.class);


    private final BlogPhotoService blogPhotoService;



    @DeleteMapping("/{imageId}")
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
                                                                "status": "error",
                                                                "message": "Blog image not found"
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
                                                                "status": "error",
                                                                "message": "Internal server error"
                                                            }
                                                            """
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<?> deleteImage(@PathVariable int imageId) {
        blogPhotoService.deleteImage(imageId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{imageId}")
    @Operation(
            summary = "Retrieve a blog photo by ID",
            description = "Endpoint for retrieving a blog photo by ID",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Blog photo found",
                            content = @Content(
                                    schema = @Schema(implementation = UserProfilePhotoDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Blog photo not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "status": "error",
                                                                "message": "Blog photo not found"
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
                                                                "status": "error",
                                                                "message": "Internal server error"
                                                            }
                                                            """
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<?> getPhotoById(@PathVariable int imageId) {
        byte[] imageData = blogPhotoService.downloadImage(imageId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }
}
