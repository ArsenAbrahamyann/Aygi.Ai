package com.example.demo.dto;

import com.example.demo.entity.Blog;
import com.example.demo.entity.BlogPhoto;
import com.example.demo.entity.Category;
import org.springframework.stereotype.Component;


import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BlogMapper {
    public BlogDTO toBlogDTO(Blog blog) {
        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setId(blog.getId());
        blogDTO.setTitle(blog.getTitle());
        blogDTO.setText(blog.getText());
        blogDTO.setCategory(blog.getCategory().getName());
        blogDTO.setCreatedDate(blog.getCreatedDate());

        BlogPhoto blogPhoto = blog.getBlogPhoto();
        if (blogPhoto != null && blogPhoto.getImageData() != null) {
            blogDTO.setBlogPhoto(blogPhoto.getImageData());
        }

        if (blog.getUser() != null) {
            blogDTO.setUserId(blog.getUser().getId());
        }

        return blogDTO;
    }

    public List<BlogDTO> toBlogDTOs(List<Blog> blogs) {
        return blogs.stream()
                .map(this::toBlogDTO)
                .collect(Collectors.toList());
    }
}
