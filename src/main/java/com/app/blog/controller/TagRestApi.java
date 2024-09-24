package com.app.blog.controller;

import com.app.blog.model.dto.TagDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tags", description = "All The Tag Endpoints")
public interface TagRestApi {

    @Operation(
            summary = "Get all tags",
            description = "Gets all tags DTOs and their data from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    List<TagDTO> getAllTags();

    @Operation(
            summary = "Get tag by id",
            description = "Gets one tag DTO and its data from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    ResponseEntity<TagDTO> getTagById(@PathVariable Long id);

    @Operation(
            summary = "Add a tag",
            description = "Adds a tag to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    ResponseEntity<TagDTO> addTag(@Valid @RequestBody TagDTO tagDTO);

    @Operation(
            summary = "Edit a tag",
            description = "Edits a tags data and saves it to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    ResponseEntity<TagDTO> editTag(@PathVariable Long id, @Valid @RequestBody TagDTO tagDTO);

    @Operation(
            summary = "Delete a blog post",
            description = "Deletes a blog post from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    void deleteTag(@PathVariable Long id);
}

