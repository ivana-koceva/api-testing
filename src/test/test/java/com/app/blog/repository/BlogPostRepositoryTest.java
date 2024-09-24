package com.app.blog.repository;

import com.app.blog.model.BlogPost;
import com.app.blog.model.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class BlogPostRepositoryTest {

    @Autowired
    private BlogPostRepository blogPostRepository;
    @Autowired
    private TagRepository tagRepository;
    BlogPost blogPost;
    Tag tag;

    @BeforeEach
    void setUp() {
        blogPost = new BlogPost("One", "Description One");
        tag = new Tag("java");
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
    public void testFindByTagFound() {
        List<BlogPost> blogPostsWithTag = blogPostRepository.findBlogPostByTagsContaining(tag);
        assertEquals(blogPostsWithTag.getFirst(), blogPost);
    }

    @Test
    public void testFindByTagNotFound() {
        Tag notFound = new Tag("c++");
        tagRepository.save(notFound);
        List<BlogPost> blogPostsWithTag = blogPostRepository.findBlogPostByTagsContaining(notFound);
        assertTrue(blogPostsWithTag.isEmpty());
    }

}
