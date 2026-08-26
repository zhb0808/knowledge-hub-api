package com.software.knowledgehub.system.service;

import com.software.knowledgehub.system.dto.CreatePermissionDTO;
import com.software.knowledgehub.system.dto.UpdatePermissionDTO;
import com.software.knowledgehub.system.entity.SysPermission;
import com.software.knowledgehub.system.vo.PermissionVO;

import java.util.List;

public interface PermissionService {

    /**
     * 创建权限。
     */
    PermissionVO createPermission(CreatePermissionDTO request);

    /**
     * 查询权限详情。
     */
    PermissionVO getPermission(Long id);

    /**
     * 查询权限列表。
     */
    List<PermissionVO> listPermissions();

    /**
     * 修改权限。
     */
    void updatePermission(Long id, UpdatePermissionDTO request);

    /**
     * 删除权限。
     */
    void deletePermission(Long id);

    /**
     * 加载用户拥有的权限。
     */
    List<SysPermission> listByUserId(Long userId);
}
