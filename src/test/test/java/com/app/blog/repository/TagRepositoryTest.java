package com.app.blog.repository;

import com.app.blog.model.BlogPost;
import com.app.blog.model.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private BlogPostRepository blogPostRepository;
    Tag tag;
    BlogPost blog;

    @BeforeEach
    void setUp() {
        tag = new Tag("java");
        blog = new BlogPost("First", "Text");
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
    public void testFindByBlogFound() {
        List<Tag> tags = tagRepository.findTagsByBlogPostsContains(blog);
        assertEquals(tags.getFirst(), tag);
    }

    @Test
    public void testFindByBlogNotFound() {
        BlogPost blogNotFound = new BlogPost("First", "Text");
        blogPostRepository.save(blogNotFound);
        List<Tag> tags = tagRepository.findTagsByBlogPostsContains(blogNotFound);
        assertTrue(tags.isEmpty());
    }

    @Test
    public void testFindByNameFound() {
        assertEquals(tagRepository.findByName("java"), Optional.of(tag));
    }

    @Test
    public void testFindByNameNotFound() {
        assertEquals(tagRepository.findByName("c#"), Optional.empty());
    }

}
