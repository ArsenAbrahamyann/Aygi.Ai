package com.example.demo.detectionHistory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;


@RestController

@RequestMapping("api/detections")
@Tag(name = "Detection History", description = "For Detection History")
@RequiredArgsConstructor
public class DetectionHistoryController {

    private final DetectionHistoryService detectionService;


    @PostMapping(value = "/save", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(description = "Endpoint for saving detection", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<Object> createDetection(@RequestPart String text,
                                                  @RequestPart MultipartFile image,
                                                  Principal principal) {
        String savedDetection = detectionService.saveDetection(text, image, principal);
        return new ResponseEntity<>(savedDetection, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(description = "Endpoint for getting all detections of the current user", security = {@SecurityRequirement(name = "bearerAuth")})
    public List<DetectionDTO> getAllDetections(Principal principal) {
        return detectionService.getAllDetections(principal);
    }

    @GetMapping("/{id}")
    @Operation(description = "Endpoint for getting a detection by id", security = {@SecurityRequirement(name = "bearerAuth")})
    public DetectionDTO getDetectionById(@PathVariable Integer id, Principal principal) {
        return detectionService.getDetectionById(id, principal);
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Endpoint for deleting a detection by id", security = {@SecurityRequirement(name = "bearerAuth")})
    public void deleteDetectionById(@PathVariable Integer id, Principal principal) {
        detectionService.deleteDetectionById(id, principal);
    }

    @GetMapping("/image/{detectionId}")
    public ResponseEntity<?> getPhotoById(@PathVariable int detectionId, Principal principal) {
        byte[] imageData = detectionService.downloadImage(detectionId, principal);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }


}