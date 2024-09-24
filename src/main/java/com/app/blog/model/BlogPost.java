package com.app.blog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String text;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name="BLOG_POST_TAGS",
            joinColumns = @JoinColumn(name = "BLOG_POSTS_ID"),
            inverseJoinColumns = @JoinColumn(name = "TAGS_ID")
    )
    private Set<Tag> tags = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlogPost blogPost = (BlogPost) o;
        return Objects.equals(id, blogPost.id) && Objects.equals(title, blogPost.title) && Objects.equals(text, blogPost.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, text);
    }
}
