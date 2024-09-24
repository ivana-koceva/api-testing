package com.app.blog.controller;

import com.app.blog.controller.impl.TagController;
import com.app.blog.model.Tag;
import com.app.blog.model.dto.TagDTO;
import com.app.blog.model.exception.TagAlreadyExistsException;
import com.app.blog.model.exception.TagNotFoundException;
import com.app.blog.service.TagService;
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
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
class TagRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;
    Tag tag;
    List<TagDTO> tags = new ArrayList<>();
    TagDTO tagDTO;
    ObjectMapper mapper = new ObjectMapper();
    ObjectWriter ow;

    @BeforeEach
    void setUp() {
        tag = Tag.builder().name("java").id(1L).build();
        tagDTO = TagDTO.builder().name(tag.getName()).build();
        tags.add(tagDTO);

        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ow = mapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    void getAllTags_Found() throws Exception {
        when(tagService.findAll()).thenReturn(tags);

        this.mockMvc.perform(get("/tags"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("java"));
    }

    @Test
    void getTag_Found() throws Exception {
        when(tagService.findById(tag.getId())).thenReturn(tagDTO);

        this.mockMvc.perform(get("/tags/1"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("java"));
    }

    @Test
    void addTag_Success() throws Exception {
        String requestJson=ow.writeValueAsString(tagDTO);

        when(tagService.create(TagDTO.builder().name(tag.getName()).build()))
                .thenReturn(tagDTO);
        this.mockMvc.perform(post("/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("java"));
    }

    @Test
    void editTag_Success() throws Exception {

        tagDTO.setName("edited");
        String requestJson=ow.writeValueAsString(tagDTO);

        when(tagService.update(tag.getId(), TagDTO.builder().name("edited").build()))
                .thenReturn(tagDTO);
        this.mockMvc.perform(put("/tags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("edited"));
    }

    @Test
    void deleteTag_Success() throws Exception {
        doNothing().when(tagService).deleteById(1L);
        this.mockMvc.perform(delete("/tags/1"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void getTag_NotFound() throws Exception {
        when(tagService.findById(2L)).thenThrow(new TagNotFoundException());

        this.mockMvc.perform(get("/tags/2"))
                .andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    void getTag_AlreadyExists() throws Exception {
        String requestJson=ow.writeValueAsString(tagDTO);

        when(tagService.create(TagDTO.builder().name(tag.getName()).build()))
                .thenThrow(new TagAlreadyExistsException());

        this.mockMvc.perform(post("/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    void addTag_InvalidInput() throws Exception {
        String requestJson = "{}";

        this.mockMvc.perform(post("/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}