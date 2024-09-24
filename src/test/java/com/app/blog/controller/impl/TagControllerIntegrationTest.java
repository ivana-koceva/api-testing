package com.app.blog.controller.impl;

import com.app.blog.model.Tag;
import com.app.blog.model.dto.TagDTO;
import com.app.blog.repository.TagRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TagControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TagRepository tagRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        tagRepository.deleteAll();
    }

    @Test
    public void testGetAllTags() throws Exception {
        Tag tag1 = new Tag();
        tag1.setName("Tag 1");
        tagRepository.save(tag1);

        Tag tag2 = new Tag();
        tag2.setName("Tag 2");
        tagRepository.save(tag2);

        mockMvc.perform(get("/tags"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Tag 1")))
                .andExpect(jsonPath("$[1].name", is("Tag 2")));
    }

    @Test
    public void testGetTagById() throws Exception {
        Tag tag = new Tag();
        tag.setName("Test Tag");
        Tag savedTag = tagRepository.save(tag);

        mockMvc.perform(get("/tags/{id}", savedTag.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Test Tag")));
    }

    @Test
    public void testAddTag() throws Exception {
        TagDTO tagDTO = new TagDTO();
        tagDTO.setName("New Tag");

        mockMvc.perform(post("/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("New Tag")));


        Tag savedTag = tagRepository.findByName("New Tag").orElse(null);
        assertThat(savedTag).isNotNull();
    }

    @Test
    public void testEditTag() throws Exception {
        Tag tag = new Tag();
        tag.setName("Old Tag");
        Tag savedTag = tagRepository.save(tag);

        TagDTO updatedTagDTO = new TagDTO();
        updatedTagDTO.setName("Updated Tag");

        mockMvc.perform(put("/tags/{id}", savedTag.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTagDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Updated Tag")));


        Tag updatedTag = tagRepository.findById(savedTag.getId()).orElse(null);
        assertThat(updatedTag).isNotNull();
        assertTrue(updatedTag.getName().equals("Updated Tag"));
    }

    @Test
    public void testDeleteTag() throws Exception {
        Tag tag = new Tag();
        tag.setName("Tag to be deleted");
        Tag savedTag = tagRepository.save(tag);

        mockMvc.perform(delete("/tags/{id}", savedTag.getId()))
                .andExpect(status().isOk());

        assertThat(tagRepository.findById(savedTag.getId())).isEmpty();
    }
}
