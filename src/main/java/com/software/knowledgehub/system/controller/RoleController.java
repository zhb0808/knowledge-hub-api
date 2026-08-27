package com.software.knowledgehub.system.controller;

import com.software.knowledgehub.audit.annotation.OperationLog;
import com.software.knowledgehub.common.response.ApiResponse;
import com.software.knowledgehub.system.dto.AssignPermissionDTO;
import com.software.knowledgehub.system.dto.CreateRoleDTO;
import com.software.knowledgehub.system.dto.UpdateRoleDTO;
import com.software.knowledgehub.system.service.RoleService;
import com.software.knowledgehub.system.vo.RoleVO;
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
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @OperationLog(module = "角色管理", action = "创建角色")
    @PostMapping
    public ApiResponse<RoleVO> createRole(@Valid @RequestBody CreateRoleDTO request) {
        return ApiResponse.success(roleService.createRole(request));
    }

    @GetMapping
    public ApiResponse<List<RoleVO>> listRoles() {
        return ApiResponse.success(roleService.listRoles());
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleVO> getRole(@PathVariable Long id) {
        return ApiResponse.success(roleService.getRole(id));
    }

    @OperationLog(module = "角色管理", action = "修改角色")
    @PutMapping("/{id}")
    public ApiResponse<Void> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleDTO request) {
        roleService.updateRole(id, request);
        return ApiResponse.success(null);
    }

    @OperationLog(module = "角色管理", action = "删除角色")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.success(null);
    }

    @OperationLog(module = "角色管理", action = "分配角色权限")
    @PutMapping("/{id}/permissions")
    public ApiResponse<Void> assignPermissions(
            @PathVariable Long id,
            @Valid @RequestBody AssignPermissionDTO request) {
        roleService.assignPermissions(id, request);
        return ApiResponse.success(null);
    }
}
