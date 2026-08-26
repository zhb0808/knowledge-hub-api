package com.software.knowledgehub.system.controller;

import com.software.knowledgehub.common.response.ApiResponse;
import com.software.knowledgehub.system.dto.CreatePermissionDTO;
import com.software.knowledgehub.system.dto.UpdatePermissionDTO;
import com.software.knowledgehub.system.service.PermissionService;
import com.software.knowledgehub.system.vo.PermissionVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ApiResponse<PermissionVO> createPermission(
            @Valid @RequestBody CreatePermissionDTO request) {
        return ApiResponse.success(permissionService.createPermission(request));
    }

    @GetMapping
    public ApiResponse<List<PermissionVO>> listPermissions() {
        return ApiResponse.success(permissionService.listPermissions());
    }

    @GetMapping("/{id}")
    public ApiResponse<PermissionVO> getPermission(@PathVariable Long id) {
        return ApiResponse.success(permissionService.getPermission(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updatePermission(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePermissionDTO request) {
        permissionService.updatePermission(id, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ApiResponse.success(null);
    }
}
