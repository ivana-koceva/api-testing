package com.app.blog.model.mapper;

import com.app.blog.model.BlogPost;
import com.app.blog.model.Tag;
import com.app.blog.model.dto.BlogPostDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class BlogPostMapper {

    public BlogPostDTO convertToDto(BlogPost blogPost) {
        BlogPostDTO blogPostDTO = BlogPostDTO.builder()
                .title(blogPost.getTitle())
                .text(blogPost.getText())
                .build();

        if (blogPost.getTags() != null) {
            blogPostDTO.setTags(blogPost.getTags().stream()
                    .map(Tag::getName)
                    .toList());
        } else {
            blogPostDTO.setTags(new ArrayList<>());
        }

        return blogPostDTO;
    }

}
