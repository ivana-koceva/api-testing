package com.app.blog.service.impl;

import com.app.blog.model.Tag;
import com.app.blog.model.dto.TagDTO;
import com.app.blog.model.exception.TagAlreadyExistsException;
import com.app.blog.model.exception.TagNotFoundException;
import com.app.blog.repository.BlogPostRepository;
import com.app.blog.repository.TagRepository;
import com.app.blog.service.TagService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagServiceImplementationTest {

    @Mock
    private TagRepository tagRepository;
    @Mock
    private BlogPostRepository blogPostRepository;
    private TagService tagService;
    AutoCloseable autoCloseable;
    Tag tag;
    TagDTO tagDTO;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        tagService = TagServiceImplementation.builder().tagRepository(tagRepository).blogPostRepository(blogPostRepository).build();
        tag = Tag.builder().name("c#").id(1L).build();
        tagDTO = TagDTO.builder().name(tag.getName()).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void findAllTagsSuccess() {
        when(tagRepository.findAll()).thenReturn(
                new ArrayList<>(Collections.singleton(tag))
        );
        assertEquals(tagService.findAll().getFirst(), TagDTO.builder().name(tag.getName()).build());
    }

    @Test
    void findTagByIdSuccess() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        assertEquals(tagService.findById(tag.getId()), TagDTO.builder().name(tag.getName()).build());
    }

    @Test
    void findTagByNameSuccess() {
        when(tagRepository.findByName(tag.getName())).thenReturn(Optional.of(tag));
        assertEquals(tagService.findByName(tag.getName()), TagDTO.builder().name(tag.getName()).build());
    }

    @Test
    void createTagSuccess() {
        when(tagRepository.save(tag)).thenReturn(tag);
        TagDTO created = tagService.create(TagDTO.builder().name(tag.getName()).build());
        assertEquals(created.getName(), tag.getName());
    }

    @Test
    void updateTagSuccess() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(tagRepository.save(tag)).thenReturn(tag);
        TagDTO edited = tagService.update(tag.getId(), TagDTO.builder().name(tag.getName()).build());
        assertEquals(edited.getName(), tag.getName());
    }

    @Test
    void deleteTagSuccess() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        doNothing().when(tagRepository).delete(tag);
        assertAll(() -> tagService.deleteById(tag.getId()));
    }

    @Test
    void tagNotFoundThrown() {
        assertThrows(TagNotFoundException.class, () -> tagService.findById(3L));
        assertThrows(TagNotFoundException.class, () -> tagService.update(3L, tagDTO));
        assertThrows(TagNotFoundException.class, () -> tagService.findByName("Not Found"));
        assertThrows(TagNotFoundException.class, () -> tagService.deleteById(3L));
    }

    @Test
    void tagAlreadyExistsThrown() {
        TagDTO created = TagDTO.builder().name(tag.getName()).build();
        when(tagRepository.findByName(tagDTO.getName())).thenReturn(Optional.ofNullable(tag));
        assertThrows(TagAlreadyExistsException.class, () -> tagService.create(created));
        assertThrows(TagAlreadyExistsException.class, () -> tagService.update(1L, created));
    }
}

