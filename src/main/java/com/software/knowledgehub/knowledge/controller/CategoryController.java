package com.software.knowledgehub.knowledge.controller;

import com.software.knowledgehub.common.response.ApiResponse;
import com.software.knowledgehub.knowledge.dto.CreateCategoryDTO;
import com.software.knowledgehub.knowledge.dto.UpdateCategoryDTO;
import com.software.knowledgehub.knowledge.service.CategoryService;
import com.software.knowledgehub.knowledge.vo.CategoryVO;
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
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ApiResponse<CategoryVO> createCategory(
            @Valid @RequestBody CreateCategoryDTO request) {
        return ApiResponse.success(categoryService.createCategory(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryVO> getCategory(@PathVariable Long id) {
        return ApiResponse.success(categoryService.getCategory(id));
    }

    @GetMapping
    public ApiResponse<List<CategoryVO>> listCategories(
            @RequestParam Long knowledgeBaseId) {
        return ApiResponse.success(categoryService.listCategories(knowledgeBaseId));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryDTO request) {
        categoryService.updateCategory(id, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success(null);
    }
}
