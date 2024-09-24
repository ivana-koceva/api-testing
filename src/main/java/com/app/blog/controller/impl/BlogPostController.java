package com.app.blog.controller.impl;
import com.app.blog.controller.BlogPostRestApi;
import com.app.blog.model.dto.BlogPostDTO;
import com.app.blog.service.BlogPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Validated
@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
public class BlogPostController implements BlogPostRestApi {
    private final BlogPostService blogPostService;
    private final Logger logger = LoggerFactory.getLogger(BlogPostController.class);

    @GetMapping()
    public List<BlogPostDTO> getBlogPosts() {
        logger.trace("BlogPostController - getBlogPosts");
        return this.blogPostService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogPostDTO> getBlogPostById(@PathVariable Long id) {
        logger.trace("BlogPostController - getBlogPostById");
        return ResponseEntity.ok().body(this.blogPostService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<BlogPostDTO> addBlogPost(@Valid @RequestBody BlogPostDTO blogPostDTO) {
        logger.trace("BlogPostController - addBlogPost");
        return ResponseEntity.ok().body(this.blogPostService.create(blogPostDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogPostDTO> editBlogPost(@PathVariable Long id, @Valid @RequestBody BlogPostDTO blogPostDTO) {
        logger.trace("BlogPostController - editBlogPost");
        return ResponseEntity.ok().body(this.blogPostService.update(id, blogPostDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BlogPostDTO> patchBlogPost(@PathVariable Long id, @RequestBody BlogPostDTO blogPostDTO) {
        logger.trace("BlogPostController - patchBlogPost");
        return ResponseEntity.ok().body(this.blogPostService.patch(id, blogPostDTO));
    }

    @PostMapping("/{id}/tags")
    public ResponseEntity<BlogPostDTO> addTagToBlogPost(@PathVariable Long id, @RequestParam List<String> tags) {
        logger.trace("BlogPostController - addTagToBlogPost");
        return ResponseEntity.ok().body(this.blogPostService.addTagToBlogPost(id, tags));
    }

    @DeleteMapping("/{id}/tags")
    public ResponseEntity<BlogPostDTO> removeTagFromBlogPost(@PathVariable Long id, @RequestParam List<String> tags) {
        logger.trace("BlogPostController - removeTagFromBlogPost");
        return ResponseEntity.ok().body(this.blogPostService.removeTagFromBlogPost(id, tags));
    }

    @DeleteMapping("/{id}")
    public void deleteBlogPost(@PathVariable Long id) {
        logger.trace("BlogPostController - deleteBlogPost");
        this.blogPostService.deleteById(id);
    }

}
