package com.app.blog.repository;

import com.app.blog.model.BlogPost;
import com.app.blog.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long>, JpaSpecificationExecutor<BlogPost> {
    List<BlogPost> findBlogPostByTagsContaining(Tag tag);
}
