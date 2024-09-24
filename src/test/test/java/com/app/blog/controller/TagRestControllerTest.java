package com.app.blog.controller;

import com.app.blog.model.Tag;
import com.app.blog.model.dto.TagDTO;
import com.app.blog.service.TagService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
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

@WebMvcTest(TagRestController.class)
public class TagRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;
    Tag tag;
    List<TagDTO> tags = new ArrayList<>();
    TagDTO tagDTO;

    @BeforeEach
    void setUp() {
        tag = new Tag("java");
        tag.setId(1L);
        tagDTO = new TagDTO(tag.getName());
        tags.add(tagDTO);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetTags() throws Exception {
        when(tagService.findAll()).thenReturn(tags);

        this.mockMvc.perform(get("/tags"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testGetTag() throws Exception {
        when(tagService.findById(tag.getId())).thenReturn(tagDTO);

        this.mockMvc.perform(get("/tags/1"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testAddTag() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(tag);

        when(tagService.create(new TagDTO(tag.getName())))
                .thenReturn(tagDTO);
        this.mockMvc.perform(post("/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testEditTag() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(tag);

        when(tagService.update(tag.getId(),new TagDTO(tag.getName())))
                .thenReturn(tagDTO);
        this.mockMvc.perform(put("/tags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testDeleteTag() throws Exception {
        when(tagService.findById(tag.getId())).thenReturn(tagDTO);

        this.mockMvc.perform(delete("/tags/1"))
                .andDo(print()).andExpect(status().isOk());
    }

}
