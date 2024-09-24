package com.app.blog.service;

import com.app.blog.model.dto.BlogPostDTO;
import java.util.List;

public interface BlogPostService {
    List<BlogPostDTO> findAll();
    BlogPostDTO findById(Long id);
    BlogPostDTO create(BlogPostDTO blogPostDTO);
    BlogPostDTO update(Long id, BlogPostDTO blogPostDTO);
    BlogPostDTO patch(Long id, BlogPostDTO blogPostDTO);
    BlogPostDTO addTagToBlogPost(Long id, List<String> tags);
    BlogPostDTO removeTagFromBlogPost(Long id, List<String> tags);
    void deleteById(Long id);

}
