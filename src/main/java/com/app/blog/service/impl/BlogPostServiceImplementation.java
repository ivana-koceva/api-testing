package com.app.blog.service.impl;

import com.app.blog.model.BlogPost;
import com.app.blog.model.Tag;
import com.app.blog.model.dto.BlogPostDTO;
import com.app.blog.model.exception.BlogPostNotFoundException;
import com.app.blog.model.exception.TagNotFoundException;
import com.app.blog.model.mapper.BlogPostMapper;
import com.app.blog.repository.BlogPostRepository;
import com.app.blog.repository.TagRepository;
import com.app.blog.service.BlogPostService;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class BlogPostServiceImplementation implements BlogPostService {
    private final BlogPostRepository blogPostRepository;
    private final TagRepository tagRepository;
    private final BlogPostMapper blogPostMapper;
    private final Logger logger = LoggerFactory.getLogger(BlogPostServiceImplementation.class);

    @Override
    public List<BlogPostDTO> findAll() {
        logger.trace("BlogPostService - findAllWithSpecifications");
        return blogPostRepository.findAll().stream().map(blogPostMapper::convertToDto).toList();
    }

    @Override
    public BlogPostDTO findById(Long id) {
        logger.trace("BlogPostService - findById");
        return this.blogPostRepository.findById(id)
                .map(blogPostMapper::convertToDto)
                .orElseThrow(() -> {
                    logger.error("BlogPostService - findById error - Blog Post Not Found");
                        return new BlogPostNotFoundException();
                });
    }

    @Override
    public BlogPostDTO create(BlogPostDTO blogPostDTO) {
        logger.trace("BlogPostService - create");
        BlogPost blogPost = BlogPost.builder().title(blogPostDTO.getTitle()).text(blogPostDTO.getText()).tags(new HashSet<>()).build();
        blogPostDTO.getTags().forEach(tag -> {
            Tag foundTag = this.tagRepository.findByName(tag).orElse(Tag.builder().name(tag).blogPosts(new HashSet<>()).build());
            blogPost.getTags().add(foundTag);
            foundTag.getBlogPosts().add(blogPost);
        });
        this.blogPostRepository.save(blogPost);
        return blogPostMapper.convertToDto(blogPost);
    }

    @Override
    public BlogPostDTO update(Long id, BlogPostDTO blogPostDTO) {
        logger.trace("BlogPostService - update");
        BlogPost blogPost = this.blogPostRepository.findById(id).orElseThrow(() -> {
            logger.error("BlogPostService - update error - Blog Post Not Found");
            return new BlogPostNotFoundException();
        });
        blogPost.setTitle(blogPostDTO.getTitle());
        blogPost.setText(blogPostDTO.getText());
        blogPostDTO.getTags().forEach(tag -> {
            Tag foundTag = this.tagRepository.findByName(tag).orElse(Tag.builder().name(tag).blogPosts(new HashSet<>()).build());
            blogPost.getTags().add(foundTag);
            foundTag.getBlogPosts().add(blogPost);
        });
        this.blogPostRepository.save(blogPost);
        return blogPostMapper.convertToDto(blogPost);
    }

    @Override
    public BlogPostDTO patch(Long id, BlogPostDTO blogPostDTO) {
        logger.trace("BlogPostService - patch");
        BlogPost blogPost = this.blogPostRepository.findById(id).orElseThrow(() -> {
            logger.error("BlogPostService - patch error - Blog Post Not Found");
            return new BlogPostNotFoundException();
        });
        if(blogPostDTO.getTitle() != null && !blogPostDTO.getTitle().isEmpty()) {
            blogPost.setTitle(blogPostDTO.getTitle());
        }
        if(blogPostDTO.getText() != null && !blogPostDTO.getText().isEmpty()) {
            blogPost.setText(blogPostDTO.getText());
        }
        if(blogPostDTO.getTags() != null && !blogPostDTO.getTags().isEmpty()) {
            blogPostDTO.getTags().forEach(tag -> {
                Tag foundTag = this.tagRepository.findByName(tag).orElse(Tag.builder().name(tag).blogPosts(new HashSet<>()).build());
                blogPost.getTags().add(foundTag);
                foundTag.getBlogPosts().add(blogPost);
            });
        }
        this.blogPostRepository.save(blogPost);
        return blogPostMapper.convertToDto(blogPost);
    }

    @Transactional
    @Override
    public BlogPostDTO addTagToBlogPost(Long id, List<String> tags) {
        logger.trace("BlogPostService - addTagToBlogPost");
        BlogPost blogPost = this.blogPostRepository.findById(id).orElseThrow(() -> {
            logger.error("BlogPostService - addTagToBlogPost error - Blog Post Not Found");
            return new BlogPostNotFoundException();
        });
        tags.forEach(tag -> {
            Tag foundTag = this.tagRepository.findByName(tag).orElse(Tag.builder().name(tag).blogPosts(new HashSet<>()).build());
            blogPost.getTags().add(foundTag);
            foundTag.getBlogPosts().add(blogPost);
        });
        this.blogPostRepository.save(blogPost);
        return blogPostMapper.convertToDto(blogPost);
    }

    @Transactional
    @Override
    public BlogPostDTO removeTagFromBlogPost(Long id, List<String> tags) {
        logger.trace("BlogPostService - removeTagFromBlogPost");
        BlogPost blogPost = this.blogPostRepository.findById(id).orElseThrow(() -> {
            logger.error("BlogPostService - removeTagFromBlogPost error - Blog Post Not Found");
            return new BlogPostNotFoundException();
        });
        tags.forEach(tag -> {
            Tag foundTag = this.tagRepository.findByName(tag).orElseThrow(() -> {
                logger.error("BlogPostService - removeTagFromBlogPost error - Tag Not Found");
                return new TagNotFoundException();
            });
            blogPost.getTags().remove(foundTag);
            foundTag.getBlogPosts().remove(blogPost);
        });
        this.blogPostRepository.save(blogPost);
        return blogPostMapper.convertToDto(blogPost);
    }

    @Override
    public void deleteById(Long id) {
        logger.trace("BlogPostService - deleteById");
        BlogPost blogToBeDeleted = this.blogPostRepository.findById(id).orElseThrow(() -> {
            logger.error("BlogPostService - deleteById error - Blog Post Not Found");
            return new BlogPostNotFoundException();
        });
        this.tagRepository.findTagsByBlogPostsContains(blogToBeDeleted).forEach(tag -> tag.getBlogPosts().remove(blogToBeDeleted));
        this.blogPostRepository.deleteById(id);
    }
}
