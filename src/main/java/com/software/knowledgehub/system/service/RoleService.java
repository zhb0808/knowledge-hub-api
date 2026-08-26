package com.software.knowledgehub.system.service;

import com.software.knowledgehub.system.dto.AssignPermissionDTO;
import com.software.knowledgehub.system.dto.CreateRoleDTO;
import com.software.knowledgehub.system.dto.UpdateRoleDTO;
import com.software.knowledgehub.system.vo.RoleVO;

import java.util.List;

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
     * 查询角色列表。
     */
    List<RoleVO> listRoles();

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
