package com.example.demo.web;

import com.example.demo.dto.BlogDTO;
import com.example.demo.dto.DiaryDTO;
import com.example.demo.dto.DiaryPhotoDTO;
import com.example.demo.dto.UserProfilePhotoDTO;
import com.example.demo.entity.Diary;
import com.example.demo.entity.DiaryPhoto;
import com.example.demo.payload.request.AddPlanedWorkRequest;
import com.example.demo.payload.request.NewDiary;
import com.example.demo.services.DiaryService;
import com.example.demo.services.PlanedWorksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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


import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/diary")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Diary", description = "Diary Description")
public class DiaryController {
    private final DiaryService diaryService;
    private final PlanedWorksService planedWorksService;
    private final ModelMapper mapper;



    @GetMapping("/all")
    @Operation(
            description = "Endpoint for retrieving all diaries belonging to the current user",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Diaries found",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = DiaryDTO.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Diaries not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """ 
                            {
                                "status" : "error",
                                "message" : "Diaries not found"
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
    public ResponseEntity<?> getAllDiariesByUser(Principal principal) {
        List<DiaryDTO> allDiariesByUser = diaryService.getAllDiariesByUser(principal);
        return ResponseEntity.ok(allDiariesByUser);
    }
    @GetMapping("all/photos")
    @Operation(
            summary = "Get all Diary by user",
            description = "Endpoint for retrieving all diary belonging to the current user",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Diaries found",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = DiaryPhotoDTO.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Diaries not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                {
                                                    "status" : "error",
                                                    "message" : "Diaries not found"
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
    public ResponseEntity<?> getAllPhotoDiaryByUser(Principal principal) {
        List<DiaryPhotoDTO> allDiariesByUser = diaryService.getAllPhotosDiaryByUser(principal);
        if (allDiariesByUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", "error", "message", "Diaries not found"));
        }
        return ResponseEntity.ok(allDiariesByUser);
    }



    @GetMapping("/{id}")
    @Operation(
            description = "Endpoint for retrieving a diary entry by ID",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Diary entry found",
                            content = @Content(
                                    schema = @Schema(implementation = Diary.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Diary entry not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """ 
                            {
                                "status" : "error",
                                "message" : "Diary entry not found"
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
    public ResponseEntity<?> getDiaryById(@PathVariable Integer id) {
        DiaryDTO byId = diaryService.getDiaryDTO(id);
        return ResponseEntity.ok(byId);
    }



    @PatchMapping(value = "/{id}/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(
            description = "Endpoint for updating a diary entry by ID",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Diary entry updated successfully",
                            content = @Content(
                                    schema = @Schema(implementation = Diary.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Diary entry not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """ 
                            {
                                "status" : "error",
                                "message" : "Diary entry not found"
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
    public ResponseEntity<?> updateDiary(@PathVariable Integer id,
                                         @RequestPart(value = "name", required = false) String name,
                                         @RequestPart(value = "about", required = false) String about,
                                         Principal principal)
    {
        if(name != null && about != null)
        {
            NewDiary newDiary = new NewDiary(name, about);
            DiaryDTO update = diaryService.update(id, newDiary, principal);
            return ResponseEntity.ok(update);
        }
        else if(name != null && about == null)
        {
            DiaryDTO updateByName = diaryService.updateName(id, name,principal);
            return ResponseEntity.ok(updateByName);
        }
        else if(about != null && name == null)
        {
            DiaryDTO updateByAbout = diaryService.updateAbout(id, about,principal);
            return ResponseEntity.ok(updateByAbout);
        }
        return ResponseEntity.ok("No updates is needed");
    }

    @PostMapping(value = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(
            description = "Endpoint for creating a new diary entry",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Diary entry created successfully",
                            content = @Content(
                                    schema = @Schema(implementation = DiaryDTO.class)
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
                                "message" : "Invalid request"
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
            @RequestPart MultipartFile photo,
            @RequestPart String name,
            @RequestPart String about,
            @RequestParam boolean isPublic,
            Principal principal
    ) {
        NewDiary newDiary = new NewDiary(name, about);
        newDiary.setPublic(isPublic);
        String diary = diaryService.createDiary(newDiary, photo, principal);
        return new ResponseEntity<>(newDiary, HttpStatus.CREATED);
    }



    @DeleteMapping("/{id}")
    @Operation(
            description = "Endpoint for deleting a diary entry by ID",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Diary entry successfully deleted"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Diary entry not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """ 
                            {
                                "status" : "error",
                                "message" : "Diary entry not found"
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
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        diaryService.delete(id);
        return ResponseEntity.ok().build();
    }



    @PostMapping("add/{diaryId}/planedWork")
    @Operation(
            description = "Endpoint for adding planned works to a diary entry",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request body for adding planned works to a diary entry",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AddPlanedWorkRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Add Planned Works",
                                            description = "Add planned works request body",
                                            value = """
                        {
                          "workDescription": "string",
                          "date": "2024-03-20",
                          "location": "string"
                        }
                        """
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Planned works added successfully",
                            content = @Content(
                                    schema = @Schema(implementation = DiaryDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Diary entry not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """ 
                            {
                                "status" : "error",
                                "message" : "Diary entry not found"
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
    public ResponseEntity<?> addPlanedWorks(@PathVariable Integer diaryId, @RequestBody @NonNull AddPlanedWorkRequest plannedWorks) {
        Diary diary = planedWorksService.addWorkInUser(diaryId, plannedWorks);
        DiaryDTO map = mapper.map(diary, DiaryDTO.class);
        return ResponseEntity.ok(map);
    }



    @DeleteMapping("add/{diaryId}/{planedWorId}")
    @Operation(
            description = "Endpoint for removing planned works from a diary entry",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Planned work removed successfully",
                            content = @Content(
                                    schema = @Schema(implementation = DiaryDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Diary entry or planned work not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """ 
                            {
                                "status" : "error",
                                "message" : "Diary entry or planned work not found"
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
    public ResponseEntity<?> removePlanedWorks(@PathVariable Integer diaryId, @PathVariable Integer planedWorId) {
        Diary diary = planedWorksService.removePlanedWork(diaryId, planedWorId);
        DiaryDTO map = mapper.map(diary, DiaryDTO.class);
        return ResponseEntity.ok(map);
    }



    @PatchMapping("/{id}/updatePrivacy")
    @Operation(
            description = "Endpoint for updating privacy of diary entry by ID",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Diary entry updated successfully",
                            content = @Content(
                                    schema = @Schema(implementation = Diary.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Diary entry not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                            {
                                "status" : "error",
                                "message" : "Diary entry not found"
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
    public ResponseEntity<?> updateDiaryPrivacy(@PathVariable Integer id,
                                                @RequestParam boolean isPublic,
                                                Principal principal)
    {

        DiaryDTO update = diaryService.updateDiaryPrivacy(id, isPublic, principal);
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/image/{diaryId}")
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
    public ResponseEntity<?> deleteImage(@PathVariable Integer diaryId) {
        diaryService.deleteImage(diaryId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/image/{diaryId}")
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
    public ResponseEntity<?> getPhotoById(@PathVariable Integer diaryId) {
        byte[] imageData = diaryService.downloadImage(diaryId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }


    @PatchMapping(value = "/image/{diaryId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(
            description = "Endpoint for updating a diary photo by ID",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User profile photo updated successfully"
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

    public ResponseEntity<?> updateImage(@PathVariable Integer diaryId, @RequestPart MultipartFile photo) {
        String updateDiaryPhoto = diaryService.updateImage(diaryId, photo);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updateDiaryPhoto);
    }
}
