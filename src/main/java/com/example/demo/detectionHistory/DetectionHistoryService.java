package com.example.demo.detectionHistory;


import com.example.demo.entity.User;
import com.example.demo.exceptions.errors.BadRequestException;
import com.example.demo.exceptions.errors.NotFoundException;
import com.example.demo.geminiFlash.GeminiService;
import com.example.demo.services.UserService;
import com.example.demo.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

//@Service
//public class DetectionHistoryService {
//    @Autowired
//    private DetectionHistoryRepository detectionRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    public DetectionHistory saveDetection(Integer userId, String detectionType, String result) {
//        Optional<User> userOptional = userRepository.findUserById(userId);
//        if (userOptional.isPresent()) {
//            DetectionHistory detection = new DetectionHistory();
//            detection.setUser(userOptional.get());
//            detection.setDetectionType(detectionType);
//            detection.setResult(result);
//            detection.setTimestamp(LocalDateTime.now());
//            return detectionRepository.save(detection);
//        } else {
//            throw new NotFoundException("User with id " + userId + " not found");
//        }
//    }
//
//    public List<DetectionHistory> getDetectionHistory(Integer userId) {
//        Optional<User> userOptional = userRepository.findUserById(userId);
//        if (userOptional.isPresent()) {
//            return detectionRepository.findByUserId(userId);
//        } else {
//            throw new NotFoundException("User with id " + userId + " not found");
//        }
//    }
//
//    public void deleteDetection(Integer id) {
//        Optional<DetectionHistory> detection = detectionRepository.findById(id);
//        if (detection.isPresent()) {
//            detectionRepository.delete(detection.get());
//        } else {
//            throw new NotFoundException("Detection with id " + id + " not found");
//        }
//    }
//}



@Service
@RequiredArgsConstructor
public class DetectionHistoryService {

    private final DetectionHistoryRepository detectionRepository;
    private final DetectionPhotoRepository detectionPhotoRepository;



    private final UserService userService;

    private final GeminiService geminiService;

    private final DetectionMapper detectionMapper;




    public String saveDetection(String text, MultipartFile image, Principal principal)  {
        try {
            User user = userService.getUserByPrincipal(principal);
//        int userId = user.getId();
//            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            Detection detection = new Detection();
            detection.setText(text);
//            detection.setImageBase64(base64Image);
            detection.setUser(user);
            detectionRepository.save(detection);
            return this.uploadImage(image, detection.getId()) + detection.getId();


        } catch (IOException e) {
            throw new BadRequestException("Invalid operations with Base64");
        }


    }



    public String uploadImage(MultipartFile file, int detectionId) throws IOException
    {
        DetectionPhoto detectionPhoto = detectionPhotoRepository.save(DetectionPhoto.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes()))
//                .diary(getById(diaryId))
                        .detection(getById(detectionId))
                .build());
        getById(detectionId).setDetectionPhoto(detectionPhoto);
        if(detectionPhoto != null)     //it means it is saved in the db
        {
            return "Detection is saved successfully with id: ";
        }
        return null;
    }


    public byte[] downloadImage(Integer detectionId) {
        Optional<Detection> d = detectionRepository.findById(detectionId);
        if(d.isPresent() && d.get().getDetectionPhoto() != null)
        {
            byte[] image = ImageUtils.decompressImage(d.get().getDetectionPhoto().getImageData());
            return image;
        }
        else {
//            log.warn("Image with DiaryID {} not found", diaryId);
            throw new BadRequestException("Image not found for the given DetectionIDID");
        }
    }
    public byte[] downloadImage(Integer detectionId, Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        Optional<Detection> detection = detectionRepository.findById(detectionId);
        if (detection.isPresent() && detection.get().getDetectionPhoto() != null) {
            if (detection.get().getUser().equals(user)) {
                byte[] image = ImageUtils.decompressImage(detection.get().getDetectionPhoto().getImageData());
                return image;
            } else {
                try {
                    throw new AccessDeniedException("You do not have access to this image");
                } catch (AccessDeniedException e) {


                }
            }
        } else {
            throw new BadRequestException("Image not found for the given DetectionID");
        }
        byte[] image = ImageUtils.decompressImage(detection.get().getDetectionPhoto().getImageData());
        return image;
    }


    public Detection getById(int id) {
        return getByIdOptional(id).orElseThrow(NotFoundException::new);
    }

    public Optional<Detection> getByIdOptional(int id) {
        return detectionRepository.findById(id);
    }



    public List<DetectionDTO> getAllDetections(Principal principal) {
        User user = userService.getUserByPrincipal(principal);

        List<Detection> detections = detectionRepository.findByUserId(user.getId());
        return detectionMapper.toDetectionDTOs(detections);
    }

    public DetectionDTO getDetectionById(Integer id, Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        Detection detection = detectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Detection not found"));

        if (!detection.getUser().equals(user)) {
            try {
                throw new AccessDeniedException("You do not have access to this detection");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }

        return detectionMapper.toDetectionDTO(detection);
    }

    public void deleteDetectionById(Integer id, Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        Detection detection = detectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Detection not found"));

        if (!detection.getUser().equals(user)) {
            try {
                throw new AccessDeniedException("You do not have access to delete this detection");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }

        detectionRepository.deleteById(id);
    }
}
