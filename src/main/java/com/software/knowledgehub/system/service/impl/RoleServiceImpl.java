package com.software.knowledgehub.system.service.impl;

import com.software.knowledgehub.common.exception.BusinessException;
import com.software.knowledgehub.system.dto.AssignPermissionDTO;
import com.software.knowledgehub.system.dto.CreateRoleDTO;
import com.software.knowledgehub.system.dto.RoleQueryDTO;
import com.software.knowledgehub.system.dto.UpdateRoleDTO;
import com.software.knowledgehub.system.entity.SysPermission;
import com.software.knowledgehub.system.entity.SysRole;
import com.software.knowledgehub.system.repository.SysPermissionRepository;
import com.software.knowledgehub.system.repository.SysRoleRepository;
import com.software.knowledgehub.system.service.RoleService;
import com.software.knowledgehub.system.vo.RoleVO;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final SysRoleRepository sysRoleRepository;
    private final SysPermissionRepository sysPermissionRepository;

    /**
     * 创建角色。
     */
    @Override
    @Transactional
    public RoleVO createRole(CreateRoleDTO request) {
        if (sysRoleRepository.existsByCode(request.getCode())) {
            throw new BusinessException("角色编码已存在");
        }

        SysRole role = new SysRole();
        role.setCode(request.getCode());
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        return toRoleVO(sysRoleRepository.save(role));
    }

    /**
     * 查询角色详情。
     */
    @Override
    public RoleVO getRole(Long id) {
        return toRoleVO(sysRoleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("角色不存在")));
    }

    /**
     * 分页查询角色。
     */
    @Override
    public Page<RoleVO> listRoles(RoleQueryDTO request, Pageable pageable) {
        Specification<SysRole> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (request.getCode() != null && !request.getCode().isBlank()) {
                String code = "%" + request.getCode().strip().toLowerCase(Locale.ROOT) + "%";
                predicates.add(builder.like(builder.lower(root.get("code")), code));
            }
            if (request.getName() != null && !request.getName().isBlank()) {
                String name = "%" + request.getName().strip().toLowerCase(Locale.ROOT) + "%";
                predicates.add(builder.like(builder.lower(root.get("name")), name));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };

        // 根据角色编码和名称分页加载角色。
        return sysRoleRepository.findAll(specification, pageable).map(this::toRoleVO);
    }

    /**
     * 修改角色资料。
     */
    @Override
    @Transactional
    public void updateRole(Long id, UpdateRoleDTO request) {
        SysRole role = sysRoleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("角色不存在"));
        role.setName(request.getName());
        role.setDescription(request.getDescription());
    }

    /**
     * 删除角色。
     */
    @Override
    @Transactional
    public void deleteRole(Long id) {
        SysRole role = sysRoleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("角色不存在"));
        sysRoleRepository.delete(role);
    }

    /**
     * 为角色重新分配权限。
     */
    @Override
    @Transactional
    public void assignPermissions(Long id, AssignPermissionDTO request) {
        SysRole role = sysRoleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("角色不存在"));

        List<SysPermission> permissions = sysPermissionRepository.findAllById(request.getPermissionIds());
        if (permissions.size() != request.getPermissionIds().size()) {
            throw new BusinessException("权限不存在");
        }
        role.setPermissions(new HashSet<>(permissions));
    }

    private RoleVO toRoleVO(SysRole role) {
        return new RoleVO(
                role.getId(),
                role.getCode(),
                role.getName(),
                role.getDescription(),
                role.getPermissions().stream().map(SysPermission::getId).collect(java.util.stream.Collectors.toSet()),
                role.getCreatedTime(),
                role.getUpdatedTime()
        );
    }
}
