package com.app.blog.controller;
import com.app.blog.model.Tag;
import com.app.blog.model.dto.BlogPostDTO;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.app.blog.model.BlogPost;
import com.app.blog.service.BlogPostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BlogPostRestController.class)
class BlogPostRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BlogPostService blogPostService;
    BlogPost blogPostOne;
    BlogPost blogPostTwo;
    BlogPostDTO blogPostDTOOne;
    BlogPostDTO blogPostDTOTwo;
    Tag tag;
    List<BlogPostDTO> blogPosts = new ArrayList<>();
    List<BlogPostDTO> blogPostsWithTag = new ArrayList<>();
    List<String> tags = new ArrayList<>();

    @BeforeEach
    void setUp() {
        blogPostOne = new BlogPost("One", "Description One");
        blogPostTwo = new BlogPost("Two", "Description Two");
        tag = new Tag("java");
        blogPostOne.setId(1L);
        blogPostOne.getTags().add(tag);
        blogPostTwo.setId(2L);
        blogPostDTOOne = new BlogPostDTO("One", "Description One");
        blogPostDTOTwo = new BlogPostDTO("Two", "Description Two");
        blogPosts.add(blogPostDTOOne);
        blogPosts.add(blogPostDTOTwo);
        blogPostsWithTag.add(blogPostDTOOne);
        tags.add(tag.getName());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetBlogPosts() throws Exception {
       when(blogPostService.findAll()).thenReturn(blogPosts);

        this.mockMvc.perform(get("/blogs"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testGetBlogPostsWithLimit() throws Exception {
        when(blogPostService.findAllWithLimit(10)).thenReturn(blogPosts);

        this.mockMvc.perform(get("/blogs").param("limit", "10"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testGetBlogPostsWithTagAndLimit() throws Exception {
        when(blogPostService.findAllByTagWithLimit("java", 10)).thenReturn(blogPosts);

        this.mockMvc.perform(get("/blogs").param("tagName", "java").param("limit", "10"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testGetBlogPostsWithTagNumberAndLimit() throws Exception {
        when(blogPostService.findAllByTagAndTagNumber("java", 1)).thenReturn(blogPosts);

        this.mockMvc.perform(get("/blogs").param("tagNumber", "1").param("limit", "10"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testGetBlogPostsWithTagNumberAndLimitAndTag() throws Exception {
        when(blogPostService.findAllByTagWithLimitAndTagNumberGreaterOrEqual(10,"java", 1)).thenReturn(blogPosts);

        this.mockMvc.perform(get("/blogs").param("tagName", "java").param("tagNumber", "1").param("limit", "10"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testGetBlogPostsByTag() throws Exception {
        when(blogPostService.findAllByTag(tag.getName())).thenReturn(blogPostsWithTag);

        this.mockMvc.perform(get("/blogs").param("tagName", "java"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testGetBlogPostsByTagAndTagNumber() throws Exception {
        when(blogPostService.findAllByTagAndTagNumber("java", 1)).thenReturn(blogPostsWithTag);

        this.mockMvc.perform((get("/blogs").param("tagName", "java")).param("tagNumber", "1"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testGetBlogPostsByTagNumber() throws Exception {
        when(blogPostService.findAllByTagNumberGreaterOrEqual(1)).thenReturn(blogPostsWithTag);

        this.mockMvc.perform(get("/blogs").param("tagNumber", "1"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testGetBlogPost() throws Exception {
        when(blogPostService.findById(1L)).thenReturn(blogPostDTOOne);

        this.mockMvc.perform(get("/blogs/1"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testAddBlogPost() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        BlogPostDTO blogPostDTO = new BlogPostDTO(blogPostOne.getTitle(), blogPostOne.getText(), List.of("java"));

        String requestJson=ow.writeValueAsString(blogPostDTO);

        when(blogPostService.create(blogPostDTO))
                .thenReturn(blogPostDTOOne);
        this.mockMvc.perform(post("/blogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testEditBlogPost() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        BlogPostDTO blogPostDTO = new BlogPostDTO(blogPostOne.getTitle(), blogPostOne.getText(), List.of("java"));

        String requestJson=ow.writeValueAsString(blogPostDTO);

        when(blogPostService.update(blogPostOne.getId(), blogPostDTO))
                .thenReturn(blogPostDTOOne);
        this.mockMvc.perform(put("/blogs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testPatchBlogPost() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        BlogPostDTO blogPostDTO = new BlogPostDTO(blogPostOne.getTitle(), blogPostOne.getText(), List.of("java"));

        String requestJson=ow.writeValueAsString(blogPostDTO);

        when(blogPostService.patch(blogPostOne.getId(), blogPostDTO))
                .thenReturn(blogPostDTOOne);
        this.mockMvc.perform(patch("/blogs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testAddTagToBlogPost() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(blogPostOne);

        Tag found = new Tag("Python");
        found.setId(2L);
        tags.add(found.getName());

        blogPostOne.getTags().add(found);
        when(blogPostService.addTagToBlogPost(1L, tags)).thenReturn(blogPostDTOOne);

        this.mockMvc.perform(post("/blogs/1/tags").param("tags", "java, Python")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testRemoveTagFromBlogPost() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(blogPostOne);

        tag.setId(1L);
        when(blogPostService.removeTagFromBlogPost(1L, tags)).thenReturn(blogPostDTOOne);

        this.mockMvc.perform(delete("/blogs/1/tags").param("tags", "java")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk());
    }



    @Test
    void testDeleteBlogPost() throws Exception {
        when(blogPostService.findById(1L)).thenReturn(blogPostDTOOne);

        this.mockMvc.perform(delete("/blogs/1"))
                .andDo(print()).andExpect(status().isOk());
    }
}