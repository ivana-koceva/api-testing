package com.app.blog.service.impl;

import com.app.blog.model.BlogPost;
import com.app.blog.model.Tag;
import com.app.blog.model.dto.BlogPostDTO;
import com.app.blog.repository.BlogPostRepository;
import com.app.blog.repository.TagRepository;
import com.app.blog.service.BlogPostService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlogPostServiceImplementationTest {

    @Mock
    private BlogPostRepository blogPostRepository;
    @Mock
    private TagRepository tagRepository;
    private BlogPostService blogPostService;
    AutoCloseable autoCloseable;
    BlogPost blogPost;
    Tag tag;
    List<String> tags = new ArrayList<>();

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        blogPostService = new BlogPostServiceImplementation(blogPostRepository, tagRepository);
        blogPost = new BlogPost("One", "Description One");
        tag = new Tag("c#");
        tag.setId(1L);
        tagRepository.save(tag);
        blogPost.getTags().add(tag);
        tags.add(tag.getName());
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }
    @Test
    void testFindAllBlogPosts() {
        when(blogPostRepository.findAll()).thenReturn(
                new ArrayList<BlogPost>(Collections.singleton(blogPost))
        );
        assertEquals(blogPostService.findAll().getFirst(), new BlogPostDTO(blogPost.getTitle(), blogPost.getText(), blogPost.getTags().stream().map(Tag::getName).collect(Collectors.toList())));
    }

    @Test
    void testFindAllWithLimitBlogPostsWithLimit() {
        int limit=10;
        when(blogPostRepository.findAll()).thenReturn(
                new ArrayList<BlogPost>(Collections.singleton(blogPost))
        );
        assertEquals(blogPostService.findAllWithLimit(limit).getFirst(),  new BlogPostDTO(blogPost.getTitle(), blogPost.getText().substring(0, limit-3)+"...", blogPost.getTags().stream().map(Tag::getName).collect(Collectors.toList())));
    }

    @Test
    void testFindAllWithLimitBlogPostsBelowLimit() {
        int limit = 50;
        when(blogPostRepository.findAll()).thenReturn(
                new ArrayList<BlogPost>(Collections.singleton(blogPost))
        );
        assertEquals(blogPostService.findAllWithLimit(limit).getFirst(), new BlogPostDTO(blogPost.getTitle(), blogPost.getText(), blogPost.getTags().stream().map(Tag::getName).collect(Collectors.toList())));
    }

    @Test
    void testFindBlogPostByTag() {
        when(tagRepository.findByName(tag.getName())).thenReturn(Optional.of(tag));
        when(blogPostRepository.findBlogPostByTagsContaining(tag)).thenReturn(
                new ArrayList<BlogPost>(Collections.singleton(blogPost))
        );
        assertEquals(blogPostService.findAllByTag(tag.getName()).getFirst(),  new BlogPostDTO(blogPost.getTitle(), blogPost.getText(), blogPost.getTags().stream().map(Tag::getName).collect(Collectors.toList())));
    }

    @Test
    void testFindBlogPostByTagWithLimit() {
        int limit=10;
        when(tagRepository.findByName(tag.getName())).thenReturn(Optional.of(tag));
        when(blogPostRepository.findBlogPostByTagsContaining(tag)).thenReturn(
                new ArrayList<BlogPost>(Collections.singleton(blogPost))
        );
        assertEquals(blogPostService.findAllByTagWithLimit(tag.getName(), limit).getFirst(),  new BlogPostDTO(blogPost.getTitle(), blogPost.getText().substring(0, limit-3)+"...", blogPost.getTags().stream().map(Tag::getName).collect(Collectors.toList())));
    }

    @Test
    void testFindBlogPostByTagNumberWithLimit() {
        int limit=10;
        when(blogPostRepository.findAll()).thenReturn(
                new ArrayList<BlogPost>(Collections.singleton(blogPost))
        );
        when(tagRepository.findByName(tag.getName())).thenReturn(Optional.of(tag));
        when(blogPostRepository.findBlogPostByTagsContaining(tag)).thenReturn(
                new ArrayList<BlogPost>(Collections.singleton(blogPost))
        );
        assertEquals(blogPostService.findAllByTagNumberWithLimit(1, limit).getFirst(),  new BlogPostDTO(blogPost.getTitle(), blogPost.getText().substring(0, limit-3)+"...", blogPost.getTags().stream().map(Tag::getName).collect(Collectors.toList())));
    }

    @Test
    void testFindBlogPostByTagNumberWithLimitAndTag() {
        int limit=10;
        when(blogPostRepository.findAll()).thenReturn(
                new ArrayList<BlogPost>(Collections.singleton(blogPost))
        );
        when(tagRepository.findByName(tag.getName())).thenReturn(Optional.of(tag));
        when(blogPostRepository.findBlogPostByTagsContaining(tag)).thenReturn(
                new ArrayList<BlogPost>(Collections.singleton(blogPost))
        );
        assertEquals(blogPostService.findAllByTagWithLimitAndTagNumberGreaterOrEqual(limit, tag.getName(), 1).getFirst(),  new BlogPostDTO(blogPost.getTitle(), blogPost.getText().substring(0, limit-3)+"...", blogPost.getTags().stream().map(Tag::getName).collect(Collectors.toList())));
    }

    @Test
    void testFindBlogPostByTagNumberAndTag() {
        when(blogPostRepository.findAll()).thenReturn(
                new ArrayList<BlogPost>(Collections.singleton(blogPost))
        );
        when(tagRepository.findByName(tag.getName())).thenReturn(Optional.of(tag));
        when(blogPostRepository.findBlogPostByTagsContaining(tag)).thenReturn(
                new ArrayList<BlogPost>(Collections.singleton(blogPost))
        );
        assertEquals(blogPostService.findAllByTagAndTagNumber(tag.getName(), 1).getFirst(),  new BlogPostDTO(blogPost.getTitle(), blogPost.getText(), blogPost.getTags().stream().map(Tag::getName).collect(Collectors.toList())));
    }

    @Test
    void testFindBlogPostByTagNumber() {
        when(blogPostRepository.findAll()).thenReturn(
                new ArrayList<BlogPost>(Collections.singleton(blogPost))
        );
        List<BlogPostDTO> blogPostsWithTag = blogPostService.findAllByTagNumberGreaterOrEqual(1);
        assertEquals(blogPostsWithTag.getFirst(),  new BlogPostDTO(blogPost.getTitle(), blogPost.getText(), blogPost.getTags().stream().map(Tag::getName).collect(Collectors.toList())));
    }

    @Test
    void testFindBlogPostByTagNumberNotFound() {
        when(blogPostRepository.findAll()).thenReturn(
                new ArrayList<BlogPost>(Collections.singleton(blogPost))
        );
        List<BlogPostDTO> blogPostsWithTag = blogPostService.findAllByTagNumberGreaterOrEqual(2);
        assertTrue(blogPostsWithTag.isEmpty());
    }

    @Test
    void testFindBlogPostById() {
        when(blogPostRepository.findById(blogPost.getId())).thenReturn(Optional.of(blogPost));
        assertEquals(blogPostService.findById(blogPost.getId()),  new BlogPostDTO(blogPost.getTitle(), blogPost.getText(), blogPost.getTags().stream().map(Tag::getName).collect(Collectors.toList())));
    }

    @Test
    void testCreateBlogPost() {
       when(blogPostRepository.save(blogPost)).thenReturn(blogPost);
        BlogPostDTO created = blogPostService.create(new BlogPostDTO(blogPost.getTitle(), blogPost.getText(), tags));
        created.getTags().add(tag.getName());
        assertEquals(created.getTitle(), blogPost.getTitle());
    }

    @Test
    void testUpdateBlogPost() {
        blogPost.setId(1L);
        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(blogPost));
        when(blogPostRepository.save(blogPost)).thenReturn(blogPost);
        BlogPostDTO edited = blogPostService.update(blogPost.getId(), new BlogPostDTO("Edited", blogPost.getText(), tags));
        assertEquals(edited.getTitle(), blogPost.getTitle());
    }

    @Test
    void testPatchTitleBlogPost() {
        blogPost.setId(1L);
        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(blogPost));
        when(blogPostRepository.save(blogPost)).thenReturn(blogPost);
        BlogPostDTO edited = blogPostService.patch(blogPost.getId(), new BlogPostDTO(blogPost.getTitle(), null, null));
        assertEquals(edited.getTitle(), blogPost.getTitle());
    }

    @Test
    void testPatchTextBlogPost() {
        blogPost.setId(1L);
        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(blogPost));
        when(blogPostRepository.save(blogPost)).thenReturn(blogPost);
        BlogPostDTO edited = blogPostService.patch(blogPost.getId(), new BlogPostDTO(null, blogPost.getText(), null));
        assertEquals(edited.getText(), blogPost.getText());
    }

    @Test
    void testPatchTagsBlogPost() {
        blogPost.setId(1L);
        when(blogPostRepository.findById(1L)).thenReturn(Optional.of(blogPost));
        when(blogPostRepository.save(blogPost)).thenReturn(blogPost);
        BlogPostDTO edited = blogPostService.patch(blogPost.getId(), new BlogPostDTO("", "", tags));
        assertEquals(edited.getTags().getFirst(), tags.getFirst());
    }

    @Test
    void testAddTagToBlogPost() {
        Tag found = new Tag("c++");
        found.setId(2L);
        tags.add(found.getName());
        when(blogPostRepository.findById(blogPost.getId())).thenReturn(Optional.of(blogPost));
        when(tagRepository.findByName(tag.getName())).thenReturn(Optional.of(tag));
        when(tagRepository.findByName(found.getName())).thenReturn(Optional.of(found));
        when(blogPostRepository.save(blogPost)).thenReturn(blogPost);
        BlogPostDTO edited = blogPostService.addTagToBlogPost(blogPost.getId(), tags);
        assertEquals(edited.getTags().getFirst(), blogPost.getTags().stream().map(Tag::getName).findFirst().get());
    }

    @Test
    void testRemoveTagFromBlogPost() {
        blogPost.setId(1L);
        when(blogPostRepository.findById(blogPost.getId())).thenReturn(Optional.of(blogPost));
        when(tagRepository.findByName(tag.getName())).thenReturn(Optional.of(tag));
        when(blogPostRepository.save(blogPost)).thenReturn(blogPost);
        BlogPostDTO edited = blogPostService.removeTagFromBlogPost(blogPost.getId(), tags);
        assertTrue(edited.getTags().isEmpty());
    }

    @Test
    void testDeleteBlog() {
        blogPost.setId(1L);
        when(blogPostRepository.findById(blogPost.getId())).thenReturn(Optional.of(blogPost));
        doNothing().when(blogPostRepository).delete(blogPost);
        assertAll(() -> blogPostService.deleteById(blogPost.getId()));
    }
}