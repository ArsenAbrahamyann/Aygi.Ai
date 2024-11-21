package com.example.demo.services;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.PostDTO;
import com.example.demo.dto.PostMapper;
import com.example.demo.entity.ActiveWorks;
import com.example.demo.entity.Diary;
import com.example.demo.entity.Post;
import com.example.demo.entity.PostPhoto;
import com.example.demo.entity.User;
import com.example.demo.exceptions.errors.BadRequestException;
import com.example.demo.exceptions.errors.PostNotFoundException;
import com.example.demo.payload.request.NewPost;
import com.example.demo.repository.PostPhotoRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.ImageUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostPhotoRepository postPhotoRepository;
    @Lazy
    private final DiaryService diaryService;
    private final UserService userService;
    private final ActiveWorksService activeWorksService;
    private final ModelMapper modelMapper;
    private final PostMapper postMapper;

    private Post mapDtoToPost(@NotNull NewPost newPost) {
        List<ActiveWorks> activeWorks = newPost.getActiveWorksList()
                != null ? newPost.getActiveWorksList() : null;
        Diary diary = diaryService.getByIdOptional(newPost.getDiaryId())
                .orElseThrow(() -> new IllegalArgumentException("diary not found"));

        return Post.builder()
                .about(newPost.getAbout())
                .activeWorks(activeWorks)
                .diary(diary)
                .build();
    }

    @Transactional
    public Post createPost(MultipartFile photo, NewPost newPost, Principal principal) {
        try {
            Post post = mapDtoToPost(newPost);
            User userByPrincipal = userService.getUserByPrincipal(principal);
            post.setUser(userByPrincipal);
            post = postRepository.save(post);
            log.info("Post created with ID: {}", post.getId());

            if (photo
                    != null
                    && ! photo.isEmpty()) {
                PostPhoto postPhoto = uploadImage(photo, post.getId());
                if (postPhoto
                        != null) {
                    post.setPostPhoto(postPhoto);
                    post = postRepository.save(post);
                    log.info("Post updated with photo ID: {}", post.getPostPhoto().getId());
                } else {
                    log.error("Failed to upload image for post: {}", post.getId());
                }
            } else {
                log.info("No image uploaded for post: {}", post.getId());
            }

            return post;
        } catch (Exception e) {
            log.error("Error creating post: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating post: "
                    + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public PostDTO getLatestPost() {
        Post post = postRepository.findTopByOrderByCreatedDateDesc()
                .orElseThrow(() -> new PostNotFoundException("No posts available"));
        return postMapper.toPostDTO(post);
    }

    @Transactional
    public PostPhoto uploadImage(MultipartFile file, Integer postId) throws IOException {
        Post post = getPostById(postId);
        if (post
                == null) {
            throw new EntityNotFoundException("Post not found with id: "
                    + postId);
        }

        PostPhoto postPhoto = PostPhoto.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes()))
                .post(post)
                .build();

        postPhoto = postPhotoRepository.save(postPhoto);

        log.info("PostPhoto saved with ID: {}", postPhoto.getId());

        post.setPostPhoto(postPhoto);
        postRepository.save(post);
        log.info("Post updated with PostPhoto ID: {}", postPhoto.getId());

        return postPhoto;
    }

    public byte[] downloadImage(Integer postId) {
        Optional<Post> d = postRepository.findById(postId);
        if (d.isPresent()
                && d.get().getPostPhoto()
                != null) {
            byte[] image = ImageUtils.decompressImage(d.get().getPostPhoto().getImageData());
            return image;
        } else {
            log.warn("Image with postID {} not found", postId);
            throw new BadRequestException("Image not found for the given postID");
        }
    }

    public Post getPostById(Integer postId, Diary diary) {
        return postRepository.findPostByIdAndDiary(postId, diary)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found for diary: "
                        + diary.getName()));
    }

    public List<PostDTO> getAllPosts() {
        List<User> all = userRepository.findAll();
        List<PostDTO> posts = new ArrayList<>();
        for (User user : all) {
            if (user != null && user.getId() != null) {
                List<PostDTO> allPostsByUser = getAllPostsByUserId(user);
                for (PostDTO postDTO : allPostsByUser) {
                    Integer diaryId = postDTO.getDiaryId();
                    Diary diaryServiceById = diaryService.getById(diaryId);
                    if (! diaryServiceById.isPublic()) {
                        postDTO.setPublic(false);
                    } else {
                        postDTO.setPublic(true);
                    }
                    List<Integer> likedUserIds = new ArrayList<>();
                    List<String> likedUsernameIds = new ArrayList<>();
                    Post post = postRepository.findById(postDTO.getId()).orElse(null);
                    if (post != null && post.getLikedUser() != null) {
                        for (User likedUser : post.getLikedUser()) {
                            likedUserIds.add(likedUser.getId());
                            likedUsernameIds.add(likedUser.getUsername());
                        }
                    }
                    postDTO.setLikedUsers(likedUserIds);
                    postDTO.setLikedUsernames(likedUsernameIds);
                }
                posts.addAll(allPostsByUser);
            }
        }
        posts.sort(Comparator.comparing(PostDTO::getCreatedDate));
        Collections.reverse(posts);

        return posts;
    }

    public List<PostDTO> getAllPostsByUser(Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        log.info("Retrieved user: {}", user);
        List<Post> posts = postRepository.findAllByUser(user);
        log.info("Posts found: {}", posts.size());
        return postMapper.toPostDTOs(posts);
    }

    private List<PostDTO> getAllPostsByUserId(User user) {
        List<Post> allPostsByUserId = postRepository.findAllPostsByUserId(user.getId());

        // Filter out posts with null users to avoid the error
        List<Post> filteredPosts = allPostsByUserId.stream()
                .filter(post -> post.getUser()
                        != null) // Ensure post has a user
                .collect(Collectors.toList());

        return postMapper.toPostDTOs(filteredPosts);
    }

    public Post getPostById(Integer postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found"));
    }

    public Post likePost(Integer postId, Integer userId) {
        Post post = getPostById(postId);
        User user = userService.getUserById(userId);
        if (post.getLikedUser().remove(user)) {
            post.setLikes(post.getLikes()
                    - 1);
        } else {
            post.setLikes(post.getLikes()
                    + 1);
            post.getLikedUser().add(user);
        }

        return postRepository.save(post);
    }

    public void deletePost(Integer postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {

            if (post.get().getPostPhoto()
                    != null) {
                postPhotoRepository.deleteById(post.get().getPostPhoto().getId());
            }
            postRepository.deleteById(postId);
            log.info("User's posty for id {id} is deleted", postId);
        } else {
            throw new BadRequestException("There is no post to delete");
        }
    }

    public List<Post> getAllPostBelongsUser(int postId, int userId) {
        return postRepository.findAllPostsByUserIdAndPostId(userId, postId);
    }

    public void markPostAsDeleted(int postId) {
        int i = postRepository.setDeletedStatusById(postId, true);
    }

    public List<PostDTO> getAllPostsForUser(Principal principal) {
        return getAllPostsByUser(principal)
                .stream()
                .map(post -> modelMapper.map(post, PostDTO.class))
                .collect(Collectors.toList());
    }

    public void deleteImage(Integer postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()
                && postOptional.get().getPostPhoto()
                != null) {
            try {
                postPhotoRepository.deleteById(Math.toIntExact(postOptional.get().getPostPhoto().getId()));
                postOptional.get().setPostPhoto(null);
                log.info("Image with PostID {} deleted successfully", postId);
            } catch (Exception e) {
                log.error("Error occurred while deleting image with PostID {}: {}", postId, e.getMessage());
                throw new RuntimeException("Failed to delete image");
            }
        } else {
            log.warn("Image with PostID {} not found", postId);
            throw new BadRequestException("Image not found for the given PostID");
        }
    }

    public String updateImage(Integer postId, MultipartFile file) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()
                && postOptional.get().getPostPhoto()
                != null) {
            try {
                byte[] compressedImageData = ImageUtils.compressImage(file.getBytes());

                postPhotoRepository.updatePostImageDataById(Math.toIntExact(postOptional.get().getPostPhoto().getId()),
                        compressedImageData);

                PostPhoto postPhoto = postOptional.get().getPostPhoto();
                postPhoto.setImageData(compressedImageData);

                return "Image for post updated successfully";
            } catch (IOException e) {
                log.error("Error occurred while updating post with postID {}: {}", postId, e.getMessage());
                throw new RuntimeException("Failed to update image");
            }
        } else {
            log.warn("Image with postID {} not found", postId);
            throw new IllegalArgumentException("Image not found for the given postID");
        }
    }

    public PostDTO getPostDTOWithPhoto(Integer id) {

        var post = getPostById(id);
        PostDTO postDTO = postMapper.toPostDTO(post);

        if (post.getPostPhoto() != null) {
            if (post.getPostPhoto().getImageData() == null) {
                postDTO.setImage(null);
            } else {
                postDTO.setImage(post.getPostPhoto().getImageData());
            }
        } else {
            postDTO.setImage(null);
        }
        List<Integer> likedUserIds = new ArrayList<>();
        List<String> likedUsernameIds = new ArrayList<>();
        if (post != null && post.getLikedUser() != null) {
            for (User likedUser : post.getLikedUser()) {
                likedUserIds.add(likedUser.getId());
                likedUsernameIds.add(likedUser.getUsername());
            }
        }
        postDTO.setLikedUsers(likedUserIds);
        postDTO.setLikedUsernames(likedUsernameIds);

        postDTO.setCreatedDate(post.getCreatedDate());
        return postDTO;
    }

    public List<PostDTO> getPostsByDiaryId(Integer diaryId) {
        List<Post> allPostsByDiaryId = postRepository.findAllPostsByDiaryId(diaryId);
        List<PostDTO> postDTOS = new ArrayList<>();
        for (Post post : allPostsByDiaryId) {
            PostDTO map = modelMapper.map(post, PostDTO.class);
            if (post.getPostPhoto()
                    != null) {
                if (post.getPostPhoto().getImageData()
                        == null) {
                    map.setImage(null);
                } else {
                    map.setImage(post.getPostPhoto().getImageData());
                }
            } else {
                map.setImage(null);
            }
            postDTOS.add(map);
        }
        return postDTOS;
    }

    public PostDTO updatePost(Integer id, String about, List<String> activeWorksListNames,
                              MultipartFile photo, Principal principal) throws IOException {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: "
                        + id));

        // Update 'about' field
        post.setAbout(about);

        // Handle image update
        if (photo
                != null
                && ! photo.isEmpty()) {
            // Delete existing image if present
            if (post.getPostPhoto()
                    != null) {
                postPhotoRepository.deleteById(post.getPostPhoto().getId());
            }
            // Upload new image
            PostPhoto postPhoto = uploadImage(photo, id);
            post.setPostPhoto(postPhoto);
        }

        // Update active works list
        if (activeWorksListNames
                != null) {
            List<ActiveWorks> activeWorksList = activeWorksListNames.stream()
                    .map(name -> {
                        ActiveWorks activeWork = new ActiveWorks();
                        activeWork.setName(name);
                        return activeWork;
                    })
                    .collect(Collectors.toList());

            // Save or update active works
            for (ActiveWorks activeWork : activeWorksList) {
                activeWorksService.save(activeWork);
            }
            post.setActiveWorks(activeWorksList);
        }

        // Save updated post
        post = postRepository.save(post);
        return postMapper.toPostDTO(post);
    }

    public PostDTO updateDiaryPrivacy(Integer id, boolean isPublic) {
        Post postById = getPostById(id);
        postById.setPublic(isPublic);
        PostDTO postDTO = postMapper.toPostDTO(postById);
        return postDTO;
    }
}

