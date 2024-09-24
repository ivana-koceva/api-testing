package com.app.blog.service.impl;

import com.app.blog.model.Tag;
import com.app.blog.model.dto.TagDTO;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TagServiceImplementationTest {

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
        tagService = new TagServiceImplementation(tagRepository, blogPostRepository);
        tag = new Tag("c#");
        tagDTO = new TagDTO(tag.getName());
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testFindAllTags() {
        when(tagRepository.findAll()).thenReturn(
                new ArrayList<Tag>(Collections.singleton(tag))
        );
        assertEquals(tagService.findAll().getFirst(), new TagDTO(tag.getName()));
    }

    @Test
    void testFindTagById() {
        tag.setId(1L);
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        assertEquals(tagService.findById(tag.getId()), new TagDTO(tag.getName()));
    }

    @Test
    void testFindTagByName() {
        when(tagRepository.findByName(tag.getName())).thenReturn(Optional.of(tag));
        assertEquals(tagService.findByName(tag.getName()), new TagDTO(tag.getName()));
    }

    @Test
    void testCreateTag() {
        when(tagRepository.save(tag)).thenReturn(tag);
        TagDTO created = tagService.create(new TagDTO(tag.getName()));
        assertEquals(created.getName(), tag.getName());
    }

    @Test
    void testUpdateTag() {
        tag.setId(1L);
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(tagRepository.save(tag)).thenReturn(tag);
        TagDTO edited = tagService.update(tag.getId(), new TagDTO(tag.getName()));
        assertEquals(edited.getName(), tag.getName());
    }

    @Test
    void testDeleteTag() {
        tag.setId(1L);
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        doNothing().when(tagRepository).delete(tag);
        assertAll(() -> tagService.deleteById(tag.getId()));
    }
}
