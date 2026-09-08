package com.software.knowledgehub.system.service;

import com.software.knowledgehub.system.dto.CreatePermissionDTO;
import com.software.knowledgehub.system.dto.PermissionQueryDTO;
import com.software.knowledgehub.system.dto.UpdatePermissionDTO;
import com.software.knowledgehub.system.entity.SysPermission;
import com.software.knowledgehub.system.vo.PermissionVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * 分页查询权限。
     */
    Page<PermissionVO> listPermissions(PermissionQueryDTO request, Pageable pageable);

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
