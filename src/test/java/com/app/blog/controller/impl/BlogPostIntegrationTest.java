package com.app.blog.controller.impl;

import com.app.blog.model.BlogPost;
import com.app.blog.model.dto.BlogPostDTO;
import com.app.blog.repository.BlogPostRepository;
import com.app.blog.repository.TagRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BlogPostIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private TagRepository tagRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        blogPostRepository.deleteAll();
        tagRepository.deleteAll();
    }

    @Test
    public void testGetAllBlogPosts() throws Exception {
        BlogPost blogPost1 = new BlogPost();
        blogPost1.setTitle("Blog Post 1");
        blogPost1.setText("Content for a single blog post.");

        blogPostRepository.save(blogPost1);

        BlogPost blogPost2 = new BlogPost();
        blogPost2.setTitle("Blog Post 2");
        blogPost2.setText("Content for a single blog post.");

        blogPostRepository.save(blogPost2);

        mockMvc.perform(get("/blogs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Blog Post 1"))
                .andExpect(jsonPath("$[1].title").value("Blog Post 2"));
    }

    @Test
    public void testGetBlogPostById() throws Exception {
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle("Single Blog Post");
        blogPost.setText("Content for a single blog post.");

        BlogPost savedBlogPost = blogPostRepository.save(blogPost);

        mockMvc.perform(get("/blogs/{id}", savedBlogPost.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Single Blog Post"))
                .andExpect(jsonPath("$.text").value("Content for a single blog post."));
    }

    @Test
    public void testCreateBlogPost() throws Exception {
        BlogPostDTO blogPostDTO = new BlogPostDTO();
        blogPostDTO.setTitle("New Blog Post");
        blogPostDTO.setText("This is the content of the new blog post.");
        blogPostDTO.setTags(List.of("tag1", "tag2"));

        mockMvc.perform(post("/blogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blogPostDTO)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Blog Post"))
                .andExpect(jsonPath("$.text").value("This is the content of the new blog post."));
    }

    @Test
    public void testUpdateBlogPost() throws Exception {
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle("New Blog Post");
        blogPost.setText("This is the content of the new blog post.");
        BlogPost savedBlogPost = blogPostRepository.save(blogPost);

        BlogPostDTO blogPostDTO = new BlogPostDTO();
        blogPostDTO.setTitle("Updated Blog Post");
        blogPostDTO.setText("This is the content of the new blog post.");

        mockMvc.perform(put("/blogs/{id}", savedBlogPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blogPostDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("Updated Blog Post")));

        BlogPost updatedBlogPost = blogPostRepository.findById(savedBlogPost.getId()).orElse(null);
        assertThat(updatedBlogPost).isNotNull();
        assertTrue(updatedBlogPost.getTitle().equals("Updated Blog Post"));
    }

    @Test
    public void testDeleteBlogPost() throws Exception {
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle("Single Blog Post");
        blogPost.setText("Content for a single blog post.");

        BlogPost savedBlog = blogPostRepository.save(blogPost);

        mockMvc.perform(delete("/blogs/{id}", savedBlog.getId()))
                .andExpect(status().isOk());

        assertThat(blogPostRepository.findById(savedBlog.getId())).isEmpty();
    }
}
