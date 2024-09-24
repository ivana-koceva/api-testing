package com.app.blog.service.impl;

import com.app.blog.model.Tag;
import com.app.blog.model.dto.TagDTO;
import com.app.blog.model.exception.TagAlreadyExistsException;
import com.app.blog.model.exception.TagNotFoundException;
import com.app.blog.repository.BlogPostRepository;
import com.app.blog.repository.TagRepository;
import com.app.blog.service.TagService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class TagServiceImplementation implements TagService {
    private final TagRepository tagRepository;
    private final BlogPostRepository blogPostRepository;
    private final Logger logger = LoggerFactory.getLogger(TagServiceImplementation.class);

    @Override
    public List<TagDTO> findAll() {
        logger.trace("TagService - findAll");
        return tagRepository.findAll().stream()
                .map(tag -> TagDTO.builder().name(tag.getName()).build())
                .toList();
    }

    @Override
    public TagDTO findById(Long id) {
        logger.trace("TagService - findById");
        return tagRepository.findById(id).map(tag -> TagDTO.builder().name(tag.getName()).build())
                .orElseThrow(() -> {
                    logger.error("TagService - findById error - Tag Not Found");
                    return new TagNotFoundException();
                });
    }

    @Override
    public TagDTO create(TagDTO tagDTO) {
        logger.trace("TagService - create");
        if(tagRepository.findByName(tagDTO.getName()).isPresent())
        {
            logger.error("TagService - create error - Tag Already Exists");
            throw new TagAlreadyExistsException();
        }
        Tag tag = Tag.builder().name(tagDTO.getName()).build();
        tagRepository.save(tag);
        return tagDTO;
    }

    @Override
    public TagDTO update(Long id, TagDTO tagDTO) {

        logger.trace("TagService - update");
        if (tagRepository.findByName(tagDTO.getName()).isPresent()) {
            logger.error("TagService - update error - Tag Already Exists");
            throw new TagAlreadyExistsException();
        }
        Tag tag = tagRepository.findById(id).orElseThrow(() -> {
            logger.error("TagService - update error - Tag Not Found");
            return new TagNotFoundException();
        });
        tag.setName(tagDTO.getName());
        tagRepository.save(tag);
        return tagDTO;
    }

    @Override
    public TagDTO findByName(String name) {
        logger.trace("TagService - findByName");
        return tagRepository.findByName(name)
                .map(tag -> TagDTO.builder().name(tag.getName()).build())
                .orElseThrow(() -> {
                    logger.error("TagService - findByName error - Tag Not Found");
                    return new TagNotFoundException();
                });
    }

    @Override
    public void deleteById(Long id) {
        logger.trace("TagService - deleteById");
        Tag tagToBeDeleted = tagRepository.findById(id).orElseThrow(() -> {
            logger.error("TagService - deleteById error - Tag Not Found");
            return new TagNotFoundException();
        });
        this.blogPostRepository.findBlogPostByTagsContaining(tagToBeDeleted).forEach(blogPost -> blogPost.getTags().remove(tagToBeDeleted));
        tagRepository.deleteById(id);
    }
}
