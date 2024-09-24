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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlogPostServiceImplementationTest {

    @Mock
    private TagRepository tagRepository;
    private final BlogPostMapper blogPostMapper = new BlogPostMapper();
    @Mock
    private BlogPostRepository blogPostRepository;
    private BlogPostService blogPostService;
    AutoCloseable autoCloseable;
    Tag tag;
    BlogPost blogPost;
    BlogPostDTO blogPostDTO;
    List<String> tags = new ArrayList<>();
    HashSet<Tag> tagSet = new HashSet<>();
    HashSet<BlogPost> blogSet = new HashSet<>();

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        blogPostService = BlogPostServiceImplementation.builder().blogPostRepository(blogPostRepository).tagRepository(tagRepository).blogPostMapper(blogPostMapper).build();
        tag = Tag.builder().id(1L).name("c#").build();
        tags.add(tag.getName());
        tagSet.add(tag);
        blogPost = BlogPost.builder().id(1L).title("title").text("text").tags(tagSet).build();
        blogPostDTO = BlogPostDTO.builder().title(blogPost.getTitle()).text(blogPost.getText()).tags(tags).build();
        blogSet.add(blogPost);
        tag.setBlogPosts(blogSet);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }


    @Test
    void findAllBlogPosts_Found() {
        when(blogPostRepository.findAll()).thenReturn(
                new ArrayList<>(Collections.singleton(blogPost))
        );
        assertEquals(blogPostService.findAll().getFirst(), BlogPostDTO.builder().title(blogPost.getTitle()).text(blogPost.getText()).tags(blogPost.getTags().stream().map(Tag::getName).collect(Collectors.toList())).build());
    }

    @Test
    void findBlogPost_ById_Found() {
        when(blogPostRepository.findById(blogPost.getId())).thenReturn(Optional.of(blogPost));
        assertEquals(blogPostService.findById(blogPost.getId()), blogPostDTO);
    }

    @Test
    void findBlogPost_NotFound() {
        assertThrows(BlogPostNotFoundException.class, () -> blogPostService.findById(3L));
        assertThrows(BlogPostNotFoundException.class, () -> blogPostService.update(3L, blogPostDTO));
        assertThrows(BlogPostNotFoundException.class, () -> blogPostService.patch(3L, blogPostDTO));
        assertThrows(BlogPostNotFoundException.class, () -> blogPostService.addTagToBlogPost(3L, tags));
        assertThrows(BlogPostNotFoundException.class, () -> blogPostService.removeTagFromBlogPost(3L, tags));
        assertThrows(BlogPostNotFoundException.class, () -> blogPostService.deleteById(3L));
    }

    @Test
    void createBlogPost_Success() {
        when(blogPostRepository.save(blogPost)).thenReturn(blogPost);
        BlogPostDTO created = blogPostService.create(BlogPostDTO.builder().title(blogPost.getTitle()).text(blogPost.getText()).tags(tags).build());
        assertEquals(created.getTitle(), blogPost.getTitle());
    }


    @Test
    void updateBlogPost_Success() {
        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(blogPost));
        when(blogPostRepository.save(blogPost)).thenReturn(blogPost);
        BlogPostDTO edited = blogPostService.update(blogPost.getId(), BlogPostDTO.builder().title("Edited").text(blogPost.getText()).tags(tags).build());
        assertEquals(edited.getTitle(), blogPost.getTitle());
    }


    @Test
    void patchTitle_InBlogPost_Success() {
        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(blogPost));
        when(blogPostRepository.save(blogPost)).thenReturn(blogPost);
        BlogPostDTO edited = blogPostService.patch(blogPost.getId(), BlogPostDTO.builder().title(blogPost.getTitle()).text(null).tags(null).build());
        assertEquals(edited.getTitle(), blogPost.getTitle());
    }


    @Test
    void patchText_InBlogPost_Success() {
        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(blogPost));
        when(blogPostRepository.save(blogPost)).thenReturn(blogPost);
        BlogPostDTO edited = blogPostService.patch(blogPost.getId(), BlogPostDTO.builder().title(null).text(blogPost.getText()).tags(null).build());
        assertEquals(edited.getText(), blogPost.getText());
    }

    @Test
    void patchTags_InBlogPost_Success() {
        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(blogPost));
        when(blogPostRepository.save(blogPost)).thenReturn(blogPost);
        BlogPostDTO edited = blogPostService.patch(blogPost.getId(), BlogPostDTO.builder().title("").text("").tags(tags).build());
        assertEquals(edited.getTags().getFirst(), tags.getFirst());
    }

    @Test
    void addTag_ToBlogPost_Success() {
        when(blogPostRepository.findById(blogPost.getId())).thenReturn(Optional.of(blogPost));
        when(tagRepository.findByName(tag.getName())).thenReturn(Optional.of(tag));
        when(blogPostRepository.save(blogPost)).thenReturn(blogPost);
        BlogPostDTO edited = blogPostService.addTagToBlogPost(blogPost.getId(), tags);
        assertEquals(edited.getTags().getFirst(), blogPost.getTags().stream().map(Tag::getName).findFirst().get());
    }

    @Test
    void removeTag_FromBlogPost_Success() {
        when(blogPostRepository.findById(blogPost.getId())).thenReturn(Optional.of(blogPost));
        when(tagRepository.findByName(tag.getName())).thenReturn(Optional.of(tag));
        when(blogPostRepository.save(blogPost)).thenReturn(blogPost);
        BlogPostDTO edited = blogPostService.removeTagFromBlogPost(blogPost.getId(), tags);
        assertTrue(edited.getTags().isEmpty());
    }


    @Test
    void deleteBlog_Success() {
        when(blogPostRepository.findById(blogPost.getId())).thenReturn(Optional.of(blogPost));
        doNothing().when(blogPostRepository).delete(blogPost);
        assertAll(() -> blogPostService.deleteById(1L));
    }
}
