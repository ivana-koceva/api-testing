package com.app.blog.controller;

import com.app.blog.controller.impl.BlogPostController;
import com.app.blog.model.BlogPost;
import com.app.blog.model.Tag;
import com.app.blog.model.dto.BlogPostDTO;
import com.app.blog.model.exception.BlogPostNotFoundException;
import com.app.blog.service.BlogPostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BlogPostController.class)
class BlogPostRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BlogPostService blogPostService;
    BlogPost blogPostOne;
    BlogPostDTO blogPostDTOOne;
    Tag tag;
    List<BlogPostDTO> blogPosts = new ArrayList<>();
    List<BlogPostDTO> blogPostsWithTag = new ArrayList<>();
    List<String> tags = new ArrayList<>();
    ObjectMapper mapper = new ObjectMapper();
    ObjectWriter ow;

    @BeforeEach
    void setUp() {
        blogPostOne = BlogPost.builder().title("One").text("Description One").tags(new HashSet<>()).build();
        tag = Tag.builder().name("java").build();
        blogPostOne.setId(1L);
        blogPostOne.getTags().add(tag);

        blogPostDTOOne = BlogPostDTO.builder().title("One").text("Description One").tags(new ArrayList<>()).build();
        blogPosts.add(blogPostDTOOne);
        blogPostsWithTag.add(blogPostDTOOne);
        tags.add(tag.getName());

        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ow = mapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    void getAllBlogPosts_Found() throws Exception {
        when(blogPostService.findAll()).thenReturn(blogPosts);

        this.mockMvc.perform(get("/blogs"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("One"))
                .andExpect(jsonPath("$[0].text").value("Description One"));
    }

    @Test
    void getBlogPost_Found() throws Exception {
        when(blogPostService.findById(1L)).thenReturn(blogPostDTOOne);

        this.mockMvc.perform(get("/blogs/1"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("One"))
                .andExpect(jsonPath("$.text").value("Description One"));
    }

    @Test
    void addBlogPost_Success() throws Exception {
        BlogPostDTO blogPostDTO = BlogPostDTO.builder().title("New Blog").text(blogPostOne.getText()).tags(List.of("java")).build();

        String requestJson=ow.writeValueAsString(blogPostDTO);

        when(blogPostService.create(blogPostDTO))
                .thenReturn(blogPostDTO);
        this.mockMvc.perform(post("/blogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Blog"))
                .andExpect(jsonPath("$.text").value("Description One"))
                .andExpect(jsonPath("$.tags[0]").value("java"));
    }

    @Test
    void editBlogPost_Success() throws Exception {
        BlogPostDTO blogPostDTO = BlogPostDTO.builder().title("Updated Blog").text(blogPostOne.getText()).tags(List.of("java")).build();

        String requestJson=ow.writeValueAsString(blogPostDTO);

        when(blogPostService.update(blogPostOne.getId(), blogPostDTO))
                .thenReturn(blogPostDTO);
        this.mockMvc.perform(put("/blogs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Blog"))
                .andExpect(jsonPath("$.text").value("Description One"))
                .andExpect(jsonPath("$.tags[0]").value("java"));
    }

    @Test
    void patchBlogPost_Success() throws Exception {
        BlogPostDTO blogPostDTO = BlogPostDTO.builder().title(blogPostOne.getTitle()).text("Updated Text").tags(List.of("java")).build();

        String requestJson=ow.writeValueAsString(blogPostDTO);

        when(blogPostService.patch(blogPostOne.getId(), blogPostDTO))
                .thenReturn(blogPostDTO);
        this.mockMvc.perform(patch("/blogs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("One"))
                .andExpect(jsonPath("$.text").value("Updated Text"))
                .andExpect(jsonPath("$.tags[0]").value("java"));
    }

    @Test
    void addTags_ToBlogPost_Success() throws Exception {
        Tag found = Tag.builder().name("python").id(2L).build();
        tags.add(found.getName());

        BlogPostDTO blogPostDTO = BlogPostDTO.builder().title(blogPostOne.getTitle()).text(blogPostOne.getText()).tags(List.of("java", "python")).build();

        when(blogPostService.addTagToBlogPost(1L, tags)).thenReturn(blogPostDTO);

        String requestJson=ow.writeValueAsString(blogPostDTO);
        this.mockMvc.perform(post("/blogs/1/tags").param("tags", "java, python")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("One"))
                .andExpect(jsonPath("$.text").value("Description One"))
                .andExpect(jsonPath("$.tags[0]").value("java"))
                .andExpect(jsonPath("$.tags[1]").value("python"));
    }

    @Test
    void removeTags_FromBlogPost_Success() throws Exception {
        String requestJson=ow.writeValueAsString(blogPostOne);

        BlogPostDTO blogPostDTO = BlogPostDTO.builder().title(blogPostOne.getTitle()).text(blogPostOne.getText()).build();

        when(blogPostService.removeTagFromBlogPost(1L, tags)).thenReturn(blogPostDTO);

        this.mockMvc.perform(delete("/blogs/1/tags").param("tags", "java")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("One"))
                .andExpect(jsonPath("$.text").value("Description One"))
                .andExpect(jsonPath("$.tags").doesNotExist());
    }

    @Test
    void deleteBlogPost_Success() throws Exception {
        doNothing().when(blogPostService).deleteById(1L);
        this.mockMvc.perform(delete("/blogs/1"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void getBlog_NotFound() throws Exception {
        when(blogPostService.findById(2L)).thenThrow(new BlogPostNotFoundException());

        this.mockMvc.perform(get("/blogs/2"))
                .andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    void addBlog_InvalidInput() throws Exception {
        String requestJson = "{}";

        this.mockMvc.perform(post("/blogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}