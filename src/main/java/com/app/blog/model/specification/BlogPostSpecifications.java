package com.app.blog.model.specification;

import com.app.blog.model.BlogPost;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class BlogPostSpecifications {

    private BlogPostSpecifications() {

    }

    public static Specification<BlogPost> hasTagName(String tagName) {
        return (Root<BlogPost> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (tagName == null || tagName.isEmpty()) {
                return null;
            }
            return cb.equal(cb.lower(root.join("tags").get("name")), tagName.toLowerCase());
        };
    }

    public static Specification<BlogPost> hasTagNumberGreaterOrEqual(Integer tagNumber) {
        return (Root<BlogPost> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (tagNumber == null) {
                return null;
            }
            return cb.greaterThanOrEqualTo(cb.size(root.get("tags")), tagNumber);
        };
    }

    public static Specification<BlogPost> hasLimit(Integer limit) {
        return (Root<BlogPost> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (limit == null || limit <= 0) {
                return null;
            }
            return null;
        };
    }
}
