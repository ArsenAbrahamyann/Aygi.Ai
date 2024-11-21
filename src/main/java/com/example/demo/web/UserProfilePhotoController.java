package com.example.demo.web;


import com.example.demo.dto.UserProfilePhotoDTO;
import com.example.demo.services.UserProfilePhotoService;
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
import java.security.Principal;

@RequiredArgsConstructor
@RequestMapping("api/images")
@RestController
@Tag(name = "User Profile Photo", description = "Endpoints for managing user profile photos")
public class UserProfilePhotoController {

    private static final Logger log = LoggerFactory.getLogger(UserProfilePhotoController.class);


    private final UserProfilePhotoService userProfilePhotoService;



    @RequestMapping(value = "/", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, method = RequestMethod.POST)
    @Operation(
            description = "Upload image to user profile",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Image successfully uploaded",
                            content = @Content(
                                    schema = @Schema(type = "string"),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Success",
                                                    value = "File uploaded successfully. Path: /path/to/uploaded/image.jpg"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request - No image provided or image upload failed",
                            content = @Content(
                                    schema = @Schema(type = "string"),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Error",
                                                    value = "Failed to upload image"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    schema = @Schema(type = "string"),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Error",
                                                    value = "Internal server error"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<Object> uploadImageToUserProfile(@RequestPart("photo") MultipartFile photo, Principal principal) throws IOException {

        String userProfilePhoto = userProfilePhotoService.uploadImage(photo, principal);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userProfilePhoto);

    }


    @DeleteMapping("/{userId}")
    @Operation(
            description = "Endpoint for deleting a user profile image by ID",
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
    public ResponseEntity<?> deleteUserProfileImage(@PathVariable int userId) {
        userProfilePhotoService.deleteImage(userId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{userId}")
    @Operation(
            description = "Endpoint for retrieving a user profile photo by UserID",
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
    public ResponseEntity<?> getUserProfilePhotoByUserId(@PathVariable int userId)
    {

        byte[] imageData= userProfilePhotoService.downloadImage(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }


    @RequestMapping(value = "/{userId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, method = RequestMethod.PUT)
    @Operation(
            description = "Endpoint for updating a user profile photo by userID",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User profile photo updated successfully",
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

    public ResponseEntity<?> updateImage(@PathVariable int userId, @RequestPart MultipartFile photo) {
        String updateUserProfilePhoto = userProfilePhotoService.updateImage(userId, photo);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updateUserProfilePhoto);
    }

}