package com.software.knowledgehub.system.service.impl;

import com.software.knowledgehub.common.exception.BusinessException;
import com.software.knowledgehub.system.dto.CreatePermissionDTO;
import com.software.knowledgehub.system.dto.PermissionQueryDTO;
import com.software.knowledgehub.system.dto.UpdatePermissionDTO;
import com.software.knowledgehub.system.entity.SysPermission;
import com.software.knowledgehub.system.repository.SysPermissionRepository;
import com.software.knowledgehub.system.service.PermissionService;
import com.software.knowledgehub.system.vo.PermissionVO;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionServiceImpl implements PermissionService {

    private final SysPermissionRepository sysPermissionRepository;

    /**
     * 创建权限。
     */
    @Override
    @Transactional
    public PermissionVO createPermission(CreatePermissionDTO request) {
        if (sysPermissionRepository.existsByCode(request.getCode())) {
            throw new BusinessException("权限编码已存在");
        }

        SysPermission permission = new SysPermission();
        permission.setCode(request.getCode());
        permission.setName(request.getName());
        permission.setMenuPath(request.getMenuPath());
        permission.setApiRules(request.getApiRules());
        permission.setParentId(request.getParentId());
        permission.setSortOrder(request.getSortOrder());
        return toPermissionVO(sysPermissionRepository.save(permission));
    }

    /**
     * 查询权限详情。
     */
    @Override
    public PermissionVO getPermission(Long id) {
        return toPermissionVO(sysPermissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("权限不存在")));
    }

    /**
     * 分页查询权限。
     */
    @Override
    public Page<PermissionVO> listPermissions(PermissionQueryDTO request, Pageable pageable) {
        Specification<SysPermission> specification = (root, query, builder) -> {
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

        // 根据权限编码和名称分页加载权限。
        return sysPermissionRepository.findAll(specification, pageable).map(this::toPermissionVO);
    }

    /**
     * 修改权限。
     */
    @Override
    @Transactional
    public void updatePermission(Long id, UpdatePermissionDTO request) {
        SysPermission permission = sysPermissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("权限不存在"));
        permission.setName(request.getName());
        permission.setMenuPath(request.getMenuPath());
        permission.setApiRules(request.getApiRules());
        permission.setParentId(request.getParentId());
        permission.setSortOrder(request.getSortOrder());
    }

    /**
     * 删除权限。
     */
    @Override
    @Transactional
    public void deletePermission(Long id) {
        SysPermission permission = sysPermissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("权限不存在"));
        sysPermissionRepository.delete(permission);
    }

    /**
     * 加载用户拥有的权限。
     */
    @Override
    public List<SysPermission> listByUserId(Long userId) {
        return sysPermissionRepository.findByUserId(userId);
    }

    private PermissionVO toPermissionVO(SysPermission permission) {
        return new PermissionVO(
                permission.getId(),
                permission.getCode(),
                permission.getName(),
                permission.getMenuPath(),
                permission.getApiRules(),
                permission.getParentId(),
                permission.getSortOrder(),
                permission.getCreatedTime(),
                permission.getUpdatedTime()
        );
    }
}
