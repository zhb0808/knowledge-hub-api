package com.software.knowledgehub.auth.service.impl;

import com.software.knowledgehub.auth.dto.LoginDTO;
import com.software.knowledgehub.auth.service.AuthService;
import com.software.knowledgehub.auth.vo.CurrentUserVO;
import com.software.knowledgehub.auth.vo.LoginVO;
import com.software.knowledgehub.security.config.JwtProperties;
import com.software.knowledgehub.security.model.AuthenticatedUser;
import com.software.knowledgehub.security.service.TokenService;
import com.software.knowledgehub.system.entity.SysUser;
import com.software.knowledgehub.system.repository.SysUserRepository;
import com.software.knowledgehub.system.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final SysUserRepository sysUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final JwtProperties jwtProperties;
    private final PermissionService permissionService;

    /**
     * 校验账号密码并创建登录状态。
     */
    @Override
    public LoginVO login(LoginDTO request) {
        // 按用户名加载登录账户。
        SysUser user = sysUserRepository.findByUsername(request.getUsername().strip().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new BadCredentialsException("用户名或密码错误"));
        if (user.getStatus() == 0 || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误");
        }

        // 密码校验成功后创建 JWT 和 Redis 登录状态。
        return new LoginVO(tokenService.createToken(user), jwtProperties.getExpiration() / 1000);
    }

    /**
     * 查询当前登录用户信息。
     */
    @Override
    public CurrentUserVO getCurrentUser(AuthenticatedUser user) {
        // 查询当前用户，保证响应字段来自用户主数据。
        SysUser currentUser = sysUserRepository.findById(user.getId())
                .orElseThrow(() -> new BadCredentialsException("登录用户不存在"));
        return new CurrentUserVO(
                currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getDisplayName(),
                permissionService.listByUserId(currentUser.getId()).stream()
                        .map(permission -> permission.getCode())
                        .distinct()
                        .sorted()
                        .toList()
        );
    }

    /**
     * 退出当前登录状态。
     */
    @Override
    public void logout(AuthenticatedUser user) {
        // 删除 Redis 中当前 Token 的登录状态。
        tokenService.deleteToken(user.getTokenId());
    }
}
