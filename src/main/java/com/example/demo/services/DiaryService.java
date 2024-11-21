package com.example.demo.services;

import com.example.demo.dto.DiaryDTO;
import com.example.demo.dto.DiaryPhotoDTO;
import com.example.demo.entity.Diary;
import com.example.demo.entity.DiaryPhoto;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.exceptions.errors.BadRequestException;
import com.example.demo.exceptions.errors.NotFoundException;
import com.example.demo.payload.request.NewDiary;
import com.example.demo.repository.DiaryPhotoRepository;
import com.example.demo.repository.DiaryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final UserService userService;
    private final ModelMapper mapper;

//    private final DiaryPhotoService diaryPhotoService;
    private final DiaryPhotoRepository diaryPhotoRepository;

    public String createDiary(NewDiary newDiary, MultipartFile photo, Principal principal) {
        try {

            User user = userService.getUserByPrincipal(principal);
            Diary diary = mapNewDiaryToDiary(newDiary, user);
            diary = diaryRepository.save(diary);
            log.info("Create diary successfully");
            return this.uploadImage(photo, diary.getId()) + diary.getId();
//            return mapper.map(diary, DiaryDTO.class);
        } catch (Exception e) {
            log.error("create diary error : {} ", e);
            throw new BadRequestException(e.getMessage());
        }

    }

    public Diary getById(int id) {
        return getByIdOptional(id).orElseThrow(NotFoundException::new);
    }

    public Optional<Diary> getByIdOptional(int id) {
        return diaryRepository.findById(id);
    }

    public DiaryDTO getDiaryDTO(int id) {
        return mapper.map(getById(id), DiaryDTO.class);
    }

    private Diary mapNewDiaryToDiary(NewDiary newDiary, User user) {
        return Diary.builder()
                .about(newDiary.getAbout())
                .isPublic(newDiary.isPublic())
                .name(newDiary.getName())
                //.photoName(photoPath)
                .user(user)
                .build();
    }
    public List<DiaryPhotoDTO> getAllPhotosDiaryByUser(Principal principal) {
        User userByPrincipal = userService.getUserByPrincipal(principal);
        List<Diary> diaries = userByPrincipal.getDiaries();
        List<DiaryPhotoDTO> diaryPhotoDTOs = new ArrayList<>();

        for (Diary diary : diaries) {
            if (diary.getDiaryPhoto().getId() != null) {
                DiaryPhotoDTO diaryPhotoDTO = new DiaryPhotoDTO();
                diaryPhotoDTO.setDiaryId(diary.getId());
                diaryPhotoDTO.setPhotos(Collections.singletonList(downloadImage(diary.getId())));
                diaryPhotoDTOs.add(diaryPhotoDTO);
            }
        }

        return diaryPhotoDTOs;
    }


    public List<Diary> getAllDiariesByUser(User user) {
        return diaryRepository.findAllByUser(user);
    }

    public List<DiaryDTO> getAllDiariesByUser(Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        List<Diary> diaries = diaryRepository.findAllByUser(user);

        return diaries.stream()
                .map(diary -> {
                    DiaryDTO diaryDTO = mapper.map(diary, DiaryDTO.class);

                    if (diary.getDiaryPhoto() != null) {
                        byte[] photoBytes = ImageUtils.decompressImage(diary.getDiaryPhoto().getImageData());
                        diaryDTO.setDiaryPhotoBase64(photoBytes);
                    }
                    List<Integer> postIds = diary.getPosts().stream()
                            .map(Post::getId)
                            .collect(Collectors.toList());
                    diaryDTO.setPostIds(postIds);

                    return diaryDTO;
                })
                .collect(Collectors.toList());
    }


    public DiaryDTO update(int id, NewDiary newDiary, Principal principal) {
        try {
            Optional<Diary> diaryOptional = diaryRepository.findById(id);
            if (diaryOptional.isPresent()) {

                List<DiaryDTO> allDiariesByUser = getAllDiariesByUser(principal);
                if (allDiariesByUser.stream().filter(x -> x.getId() == id).findAny().isEmpty()) {
                    throw new BadRequestException("User hasn't diary of id: " + id + " ");
                }

                diaryRepository.updateDiaryById(id, newDiary.getName(), newDiary.getAbout());
                diaryOptional.get().setName(newDiary.getName());
                diaryOptional.get().setAbout(newDiary.getAbout());
                log.info("User's diary for id {id} is updated", id);
                return mapper.map(diaryOptional, DiaryDTO.class);
            } else {
                throw new BadRequestException("There is no diary to update");
            }
        } catch (Exception e) {
            log.error("Update diary error : {} ", e);
            throw new BadRequestException(e.getMessage());
        }
    }
    public DiaryDTO updateName(int id, String name, Principal principal) {
        try {
            Optional<Diary> diaryOptional = diaryRepository.findById(id);
            if (diaryOptional.isPresent()) {

                List<DiaryDTO> allDiariesByUser = getAllDiariesByUser(principal);
                if (allDiariesByUser.stream().filter(x -> x.getId() == id).findAny().isEmpty()) {
                    throw new BadRequestException("User hasn't diary of id: " + id + " ");
                }

                diaryRepository.updateDiaryNameById(id, name);
                diaryOptional.get().setName(name);
                log.info("User's diary for id {id} is updated", id);
                return mapper.map(diaryOptional, DiaryDTO.class);
            } else {
                throw new BadRequestException("There is no diary to update");
            }
        } catch (Exception e) {
            log.error("Update diary error : {} ", e);
            throw new BadRequestException(e.getMessage());
        }
    }
    public DiaryDTO updateAbout(int id, String about, Principal principal) {
        try {
            Optional<Diary> diaryOptional = diaryRepository.findById(id);
            if (diaryOptional.isPresent()) {

                List<DiaryDTO> allDiariesByUser = getAllDiariesByUser(principal);
                if (allDiariesByUser.stream().filter(x -> x.getId() == id).findAny().isEmpty()) {
                    throw new BadRequestException("User hasn't diary of id: " + id + " ");
                }

                diaryRepository.updateDiaryAboutById(id, about);
                diaryOptional.get().setAbout(about);
                log.info("User's diary for id {id} is updated", id);
                return mapper.map(diaryOptional, DiaryDTO.class);
            } else {
                throw new BadRequestException("There is no diary to update");
            }
        } catch (Exception e) {
            log.error("Update diary error : {} ", e);
            throw new BadRequestException(e.getMessage());
        }
    }

    public void delete(int id) {
        Optional<Diary> diary = diaryRepository.findById(id);
        if (diary.isPresent()) {

            if(diary.get().getDiaryPhoto() != null)
            {
                diaryPhotoRepository.deleteById(diary.get().getDiaryPhoto().getId());
            }
            diaryRepository.deleteById(id);
            log.info("User's diary for id {id} is deleted", id);
        } else {
            throw new BadRequestException("There is no diary to delete");
        }
    }


    public DiaryDTO updateDiaryPrivacy(int id, boolean isPublic, Principal principal) {
        Optional<Diary> diaryOptional = diaryRepository.findById(id);
        if (diaryOptional.isPresent()) {

            List<DiaryDTO> allDiariesByUser = getAllDiariesByUser(principal);
            if (allDiariesByUser.stream().filter(x -> x.getId() == id).findAny().isEmpty()) {
                throw new BadRequestException("User hasn't diary of id: " + id + " ");
            }
            diaryRepository.updateDiaryIsPublicById(id, isPublic);
            diaryOptional.get().setPublic(isPublic);
            log.info("User's diary privacy for id {id} is updated", id);
            return mapper.map(diaryOptional, DiaryDTO.class);
        } else {
            throw new BadRequestException("There is no diary to delete");
        }
    }


    public String uploadImage(MultipartFile file, int diaryId) throws IOException
    {
        DiaryPhoto diaryPhoto = diaryPhotoRepository.save(DiaryPhoto.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes()))
                .diary(getById(diaryId))
                .build());
        getById(diaryId).setDiaryPhoto(diaryPhoto);
        if(diaryPhoto != null)     //it means it is saved in the db
        {
            return "Diary is created successfully with id: ";
        }
        return null;
    }


    public byte[] downloadImage(Integer diaryId) {
        Optional<Diary> d = diaryRepository.findById(diaryId);
        if(d.isPresent() && d.get().getDiaryPhoto() != null)
        {
            byte[] image = ImageUtils.decompressImage(d.get().getDiaryPhoto().getImageData());
            return image;
        }
        else {
            log.warn("Image with DiaryID {} not found", diaryId);
            throw new BadRequestException("Image not found for the given DiaryIDID");
        }
    }


    public void deleteImage(Integer diaryId)
    {
        Optional<Diary> diaryOptional = diaryRepository.findById(diaryId);
        if (diaryOptional.isPresent() && diaryOptional.get().getDiaryPhoto() != null) {
            try {
                diaryPhotoRepository.deleteById(diaryOptional.get().getDiaryPhoto().getId());
                diaryOptional.get().setDiaryPhoto(null);
                log.info("Image with DiaryID {} deleted successfully", diaryId);
            } catch (Exception e) {
                log.error("Error occurred while deleting image with DiaryID {}: {}",diaryId, e.getMessage());
                throw new RuntimeException("Failed to delete image");
            }
        } else {
            log.warn("Image with DiaryID {} not found", diaryId);
            throw new BadRequestException("Image not found for the given DiaryID");
        }
    }



    public String updateImage(Integer diaryId, MultipartFile file)
    {
        Optional<Diary> diaryOptional = diaryRepository.findById(diaryId);
        if (diaryOptional.isPresent() && diaryOptional.get().getDiaryPhoto() != null) {
            try {
                // Compress the new image data
                byte[] compressedImageData = ImageUtils.compressImage(file.getBytes());

                // Update the image data in the repository
                diaryPhotoRepository.updateDiaryImageDataById(diaryOptional.get().getDiaryPhoto().getId(), compressedImageData);

                // Fetch the updated entity
                DiaryPhoto diaryPhoto = diaryOptional.get().getDiaryPhoto();
                diaryPhoto.setImageData(compressedImageData);

                return "Image for diary updated successfully";
            } catch (IOException e) {
                log.error("Error occurred while updating image with DiaryID {}: {}", diaryId, e.getMessage());
                throw new RuntimeException("Failed to update image");
            }
        } else {
            log.warn("Image with DiaryID {} not found", diaryId);
            throw new IllegalArgumentException("Image not found for the given DiaryID");
        }
    }
}