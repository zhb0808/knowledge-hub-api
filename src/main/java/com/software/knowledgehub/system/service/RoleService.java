package com.software.knowledgehub.system.service;

import com.software.knowledgehub.system.dto.AssignPermissionDTO;
import com.software.knowledgehub.system.dto.CreateRoleDTO;
import com.software.knowledgehub.system.dto.RoleQueryDTO;
import com.software.knowledgehub.system.dto.UpdateRoleDTO;
import com.software.knowledgehub.system.vo.RoleVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {

    /**
     * 创建角色。
     */
    RoleVO createRole(CreateRoleDTO request);

    /**
     * 查询角色详情。
     */
    RoleVO getRole(Long id);

    /**
     * 分页查询角色。
     */
    Page<RoleVO> listRoles(RoleQueryDTO request, Pageable pageable);

    /**
     * 修改角色资料。
     */
    void updateRole(Long id, UpdateRoleDTO request);

    /**
     * 删除角色。
     */
    void deleteRole(Long id);

    /**
     * 为角色重新分配权限。
     */
    void assignPermissions(Long id, AssignPermissionDTO request);
}
