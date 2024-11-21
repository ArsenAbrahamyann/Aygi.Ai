package com.example.demo.detectionHistory;

import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DetectionMapper {

    public DetectionDTO toDetectionDTO(Detection detection) {
        DetectionDTO detectionDTO = new DetectionDTO();
        detectionDTO.setId(detection.getId());
        detectionDTO.setText(detection.getText());
        detectionDTO.setUserId(detection.getUser().getId());
        detectionDTO.setCreatedDate(detection.getCreatedDate());

        DetectionPhoto detectionPhoto = detection.getDetectionPhoto();
        if (detectionPhoto != null && detectionPhoto.getImageData() != null) {
            String base64Image = Base64.getEncoder().encodeToString(detectionPhoto.getImageData());
            detectionDTO.setImageBase64(base64Image);
        }

        return detectionDTO;
    }

    public List<DetectionDTO> toDetectionDTOs(List<Detection> detections) {
        return detections.stream().map(this::toDetectionDTO).collect(Collectors.toList());
    }
}
