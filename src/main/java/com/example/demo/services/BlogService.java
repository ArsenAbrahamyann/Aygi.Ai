package com.example.demo.services;

import com.example.demo.dto.BlogDTO;
import com.example.demo.dto.BlogMapper;
import com.example.demo.entity.Blog;
import com.example.demo.entity.BlogPhoto;
import com.example.demo.entity.User;
import com.example.demo.exceptions.errors.BadRequestException;
import com.example.demo.exceptions.errors.NotFoundException;
import com.example.demo.payload.request.NewBlog;
import com.example.demo.repository.BlogPhotoRepository;
import com.example.demo.repository.BlogRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BlogService {
    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final ModelMapper mapper;
    private final BlogMapper blogMapper;
    private final BlogPhotoRepository blogPhotoRepository;

    public BlogDTO createBlog(NewBlog newBlog, MultipartFile photo, String categoryName, Principal principal) {
        try {
            User user = userService.getUserByPrincipal(principal);
            Blog blog = mapNewBlogToBlog(newBlog, user);
            var categoryOptional = categoryRepository.findByName(categoryName);

            if (categoryOptional.isPresent()) {
                blog.setCategory(categoryOptional.get());
            }

            blog = blogRepository.save(blog);
            log.info("Created Blog successfully");

            // Upload image
            var blogDTO =  mapper.map(blog, BlogDTO.class);
            var blogPhoto = uploadImage(photo, blog.getId());
            blogDTO.setBlogPhoto(blogPhoto.getImageData());
            return blogDTO;
        } catch (Exception e) {
            log.error("Error creating blog: {}", e.getMessage());
            throw new BadRequestException("Failed to create blog");
        }
    }

    public BlogDTO updateBlog(int id, String title, String text, String categoryName, Principal principal) {
        try {
            Optional<Blog> blogOptional = blogRepository.findById(id);
            if (blogOptional.isPresent()) {
                List<BlogDTO> allBlogsByUser = getAllBlogsByUser(principal);
                if (allBlogsByUser.stream().noneMatch(blogDTO -> blogDTO.getId() == id)) {
                    throw new BadRequestException("User doesn't have blog with id: " + id);
                }

                Blog blog = blogOptional.get();
                blog.setTitle(title);
                blog.setText(text);

                // Handle categories

                var category = categoryRepository.findByName(categoryName);
                if (category.isPresent()) {
                    blog.setCategory(category.get());
                }

                blog = blogRepository.save(blog);
                log.info("Updated Blog successfully");
                return mapper.map(blog, BlogDTO.class);
            } else {
                throw new NotFoundException("Blog not found with id: " + id);
            }
        } catch (Exception e) {
            log.error("Error updating blog: {}", e.getMessage());
            throw new BadRequestException("Failed to update blog");
        }
    }

    public BlogDTO getBlogDTO(int id) {
        return mapper.map(getBlogById(id), BlogDTO.class);
    }

    public BlogDTO getBlogDTOWithPhoto(int id) {
        var blog = getBlogByIdWithPhoto(id);
        var blogDto = mapper.map(blog, BlogDTO.class);
        blogDto.setBlogPhoto(blog.getBlogPhoto().getImageData());
        return blogDto;
    }

    public List<BlogDTO> getAllBlogsByUser(Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        return blogMapper.toBlogDTOs(blogRepository.findAllByUser(user));
    }

    public void delete(int id) {
        Optional<Blog> blog = blogRepository.findById(id);
        if (blog.isPresent()) {
            if (blog.get().getBlogPhoto() != null) {
                blogPhotoRepository.deleteById(blog.get().getBlogPhoto().getId());
            }
            blogRepository.deleteById(id);
            log.info("User's blog for id {} is deleted", id);
        } else {
            throw new BadRequestException("There is no blog to delete");
        }
    }

    public byte[] downloadImage(Integer blogId) {
        Optional<Blog> blogOptional = blogRepository.findById(blogId);
        if (blogOptional.isPresent() && blogOptional.get().getBlogPhoto() != null) {
            byte[] image = ImageUtils.decompressImage(blogOptional.get().getBlogPhoto().getImageData());
            return image;
        } else {
            log.warn("Image with blogID {} not found", blogId);
            throw new BadRequestException("Image not found for the given blogID");
        }
    }

    public void deleteImage(Integer blogId) {
        Optional<Blog> blogOptional = blogRepository.findById(blogId);
        if (blogOptional.isPresent() && blogOptional.get().getBlogPhoto() != null) {
            try {
                blogPhotoRepository.deleteById(blogOptional.get().getBlogPhoto().getId());
                blogOptional.get().setBlogPhoto(null);
                log.info("Image with BlogID {} deleted successfully", blogId);
            } catch (Exception e) {
                log.error("Error occurred while deleting image with BlogID {}: {}", blogId, e.getMessage());
                throw new RuntimeException("Failed to delete image");
            }
        } else {
            log.warn("Image with BlogID {} not found", blogId);
            throw new BadRequestException("Image not found for the given BlogID");
        }
    }

    public String updateImage(Integer blogId, MultipartFile file) {
        Optional<Blog> blogOptional = blogRepository.findById(blogId);
        if (blogOptional.isPresent() && blogOptional.get().getBlogPhoto() != null) {
            try {
                byte[] compressedImageData = ImageUtils.compressImage(file.getBytes());

                blogPhotoRepository.updateBlogImageDataById(blogOptional.get().getBlogPhoto().getId(), compressedImageData);

                BlogPhoto blogPhoto = blogOptional.get().getBlogPhoto();
                blogPhoto.setImageData(compressedImageData);

                return "Image for blog updated successfully";
            } catch (IOException e) {
                log.error("Error occurred while updating blog with blogID {}: {}", blogId, e.getMessage());
                throw new RuntimeException("Failed to update image");
            }
        } else {
            log.warn("Image with blogID {} not found", blogId);
            throw new IllegalArgumentException("Image not found for the given blogID");
        }
    }

    public Blog getBlogById(int id) {
        return blogRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public Blog getBlogByIdWithPhoto(int id) {
        return blogRepository.findBlobWithPhotoById(id).orElseThrow(NotFoundException::new);
    }

    private Blog mapNewBlogToBlog(NewBlog newBlog, User user) {
        return Blog.builder()
                .title(newBlog.getTitle())
                .text(newBlog.getText())
                .user(user)
                .build();
    }

    private BlogPhoto uploadImage(MultipartFile file, int blogId) throws IOException {
        var blogPhoto = BlogPhoto.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes()))
                .blog(getBlogById(blogId))
                .build();
        return blogPhotoRepository.save(blogPhoto);
    }
}
