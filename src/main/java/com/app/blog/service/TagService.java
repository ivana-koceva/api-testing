package com.app.blog.service;

import com.app.blog.model.dto.TagDTO;
import java.util.List;

public interface TagService {
    List<TagDTO> findAll();
    TagDTO findById(Long id);
    TagDTO create(TagDTO tagDTO);
    TagDTO update(Long id, TagDTO tagDTO);
    TagDTO findByName(String name);
    void deleteById(Long id);
}
