package com.app.blog.controller.impl;

import com.app.blog.controller.TagRestApi;
import com.app.blog.model.dto.TagDTO;
import com.app.blog.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController implements TagRestApi {

    private final TagService tagService;
    private final Logger logger = LoggerFactory.getLogger(TagController.class);

    @GetMapping()
    public List<TagDTO> getAllTags() {
        logger.trace("TagController - getAllTags");
        return this.tagService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDTO> getTagById(@PathVariable Long id) {
        logger.trace("TagController - getTagById");
        return ResponseEntity.ok().body(this.tagService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<TagDTO> addTag(@Valid @RequestBody TagDTO tagDTO) {
        logger.trace("TagController - addTag");
        return ResponseEntity.ok().body(this.tagService.create(tagDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDTO> editTag(@PathVariable Long id, @Valid @RequestBody TagDTO tagDTO) {
        logger.trace("TagController - editTag");
        return ResponseEntity.ok().body(this.tagService.update(id, tagDTO));
    }

    @DeleteMapping("/{id}")
    public void deleteTag(@PathVariable Long id) {
        logger.trace("TagController - deleteTag");
        this.tagService.deleteById(id);
    }

}
