package com.app.blog.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogPostDTO {

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;
    @NotBlank(message = "Text cannot be blank")
    @Size(min = 5, max = 100, message = "Text must be between 5 and 100 characters")
    private String text;
    private List<String> tags = new ArrayList<>();

}
