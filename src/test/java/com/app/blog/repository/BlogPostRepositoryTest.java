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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BlogPostRepositoryTest {

    @Autowired
    private BlogPostRepository blogPostRepository;
    @Autowired
    private TagRepository tagRepository;
    BlogPost blogPost;
    Tag tag;

    @BeforeEach
    void setUp() {
        blogPost = BlogPost.builder().title("One").text("Description One").tags(new HashSet<>()).build();
        tag = Tag.builder().name("java").build();
        tagRepository.save(tag);
        blogPost.getTags().add(tag);
        blogPostRepository.save(blogPost);
    }

    @AfterEach
    void tearDown() {
        blogPost = null;
        tag = null;
        blogPostRepository.deleteAll();
        tagRepository.deleteAll();
    }

    @Test
    void findBlogPost_ByTag_Found() {
        List<BlogPost> blogPostsWithTag = blogPostRepository.findBlogPostByTagsContaining(tag);
        assertEquals(blogPostsWithTag.getFirst(), blogPost);
    }

    @Test
    void findBlogPost_ByTag_NotFound() {
        Tag notFound = Tag.builder().name("c++").build();
        tagRepository.save(notFound);
        List<BlogPost> blogPostsWithTag = blogPostRepository.findBlogPostByTagsContaining(notFound);
        assertTrue(blogPostsWithTag.isEmpty());
    }
}
