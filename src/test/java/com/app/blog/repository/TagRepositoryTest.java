package com.app.blog.repository;

import com.app.blog.model.BlogPost;
import com.app.blog.model.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private BlogPostRepository blogPostRepository;
    Tag tag;
    BlogPost blog;

    @BeforeEach
    void setUp() {
        tag = Tag.builder().name("java").build();
        blog = BlogPost.builder().title("First").text("Text").tags(new HashSet<>()).build();
        blog.getTags().add(tag);
        tagRepository.save(tag);
        blogPostRepository.save(blog);
    }

    @AfterEach
    void tearDown() {
        tag = null;
        blog = null;
        tagRepository.deleteAll();
        blogPostRepository.deleteAll();
    }

    @Test
    void findTag_ByBlogPost_Found() {
        List<Tag> tags = tagRepository.findTagsByBlogPostsContains(blog);
        assertEquals(tags.getFirst(), tag);
    }

    @Test
    void findTag_ByBlogPost_NotFound() {
        BlogPost blogNotFound = BlogPost.builder().title("First").text("Text").build();
        blogPostRepository.save(blogNotFound);
        List<Tag> tags = tagRepository.findTagsByBlogPostsContains(blogNotFound);
        assertTrue(tags.isEmpty());
    }

    @Test
    void findTag_ByName_Found() {
        assertEquals(tagRepository.findByName("java"), Optional.of(tag));
    }

    @Test
    void findTag_ByName_NotFound() {
        assertEquals(tagRepository.findByName("c#"), Optional.empty());
    }

}
