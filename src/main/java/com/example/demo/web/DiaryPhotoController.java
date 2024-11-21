package com.example.demo.web;

import com.example.demo.dto.UserProfilePhotoDTO;
import com.example.demo.services.DiaryPhotoService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;





@RequiredArgsConstructor
@RequestMapping("api/diaryImages")
@RestController
@Tag(name = "Diary Photo", description = "Endpoints for managing diary photos")
public class DiaryPhotoController {

    private static final Logger log = LoggerFactory.getLogger(UserProfilePhotoController.class);


    private final DiaryPhotoService diaryPhotoService;



    @DeleteMapping("/{imageId}")
    @Operation(
            description = "Endpoint for deleting a diary image by ID",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User profile image successfully deleted"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User profile image not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\n    \"status\": \"error\",\n    \"message\": \"User profile image not found\"\n}"
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
                                                    value = "{\n    \"status\": \"error\",\n    \"message\": \"Internal server error\"\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<?> deleteImage(@PathVariable int imageId) {
        diaryPhotoService.deleteImage(imageId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{imageId}")
    @Operation(
            description = "Endpoint for retrieving a diary photo by ID",
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
    public ResponseEntity<?> getPhotoById(@PathVariable int imageId) {
        byte[] imageData = diaryPhotoService.downloadImage(imageId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }

}


