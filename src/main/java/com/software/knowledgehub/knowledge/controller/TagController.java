package com.software.knowledgehub.knowledge.controller;

import com.software.knowledgehub.common.response.ApiResponse;
import com.software.knowledgehub.knowledge.dto.CreateTagDTO;
import com.software.knowledgehub.knowledge.dto.UpdateTagDTO;
import com.software.knowledgehub.knowledge.service.TagService;
import com.software.knowledgehub.knowledge.vo.TagVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ApiResponse<TagVO> createTag(@Valid @RequestBody CreateTagDTO request) {
        return ApiResponse.success(tagService.createTag(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<TagVO> getTag(@PathVariable Long id) {
        return ApiResponse.success(tagService.getTag(id));
    }

    @GetMapping
    public ApiResponse<List<TagVO>> listTags(@RequestParam Long knowledgeBaseId) {
        return ApiResponse.success(tagService.listTags(knowledgeBaseId));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateTag(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTagDTO request) {
        tagService.updateTag(id, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ApiResponse.success(null);
    }
}
