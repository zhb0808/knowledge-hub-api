package com.software.knowledgehub.system.service.impl;

import com.software.knowledgehub.common.exception.BusinessException;
import com.software.knowledgehub.system.dto.AssignRoleDTO;
import com.software.knowledgehub.system.dto.CreateUserDTO;
import com.software.knowledgehub.system.dto.UpdateUserDTO;
import com.software.knowledgehub.system.entity.SysUser;
import com.software.knowledgehub.system.entity.SysRole;
import com.software.knowledgehub.system.repository.SysUserRepository;
import com.software.knowledgehub.system.repository.SysRoleRepository;
import com.software.knowledgehub.system.service.UserService;
import com.software.knowledgehub.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final SysUserRepository sysUserRepository;
    private final SysRoleRepository sysRoleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 创建用户。
     */
    @Override
    @Transactional
    public UserVO createUser(CreateUserDTO request) {
        String username = request.getUsername().strip().toLowerCase(Locale.ROOT);

        // 检查用户名是否已经使用。
        if (sysUserRepository.existsByUsername(username)) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setDisplayName(request.getDisplayName().strip());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setStatus((short) 1);

        // 保存新用户并取得数据库生成的主键。
        SysUser savedUser = sysUserRepository.save(user);
        return toUserVO(savedUser);
    }

    /**
     * 查询用户详情。
     */
    @Override
    public UserVO getUser(Long id) {
        // 按主键加载用户。
        SysUser user = sysUserRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return toUserVO(user);
    }

    /**
     * 分页查询用户。
     */
    @Override
    public Page<UserVO> listUsers(Pageable pageable) {
        // 按创建时间倒序加载用户分页数据。
        return sysUserRepository.findAll(pageable).map(this::toUserVO);
    }

    /**
     * 修改用户资料和状态。
     */
    @Override
    @Transactional
    public void updateUser(Long id, UpdateUserDTO request) {
        // 加载需要修改的托管实体。
        SysUser user = sysUserRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        user.setDisplayName(request.getDisplayName().strip());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus());
    }

    /**
     * 删除用户。
     */
    @Override
    @Transactional
    public void deleteUser(Long id) {
        // 加载用户，避免把不存在的删除当作成功。
        SysUser user = sysUserRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        sysUserRepository.delete(user);
    }

    /**
     * 为用户重新分配角色。
     */
    @Override
    @Transactional
    public void assignRoles(Long id, AssignRoleDTO request) {
        SysUser user = sysUserRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        List<SysRole> roles = sysRoleRepository.findAllById(request.getRoleIds());
        if (roles.size() != request.getRoleIds().size()) {
            throw new BusinessException("角色不存在");
        }
        user.setRoles(new HashSet<>(roles));
    }

    private UserVO toUserVO(SysUser user) {
        return new UserVO(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail(),
                user.getStatus(),
                user.getRoles().stream()
                        .map(SysRole::getId)
                        .collect(Collectors.toSet()),
                user.getCreatedTime(),
                user.getUpdatedTime()
        );
    }

}
