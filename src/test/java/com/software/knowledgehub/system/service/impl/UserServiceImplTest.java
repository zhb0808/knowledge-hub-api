package com.software.knowledgehub.system.service.impl;

import com.software.knowledgehub.system.dto.CreateUserDTO;
import com.software.knowledgehub.system.dto.UpdateUserDTO;
import com.software.knowledgehub.system.entity.SysUser;
import com.software.knowledgehub.system.repository.SysUserRepository;
import com.software.knowledgehub.system.vo.UserVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private SysUserRepository sysUserRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldNormalizeAndCreateUser() {
        CreateUserDTO request = new CreateUserDTO();
        request.setUsername("Admin_01");
        request.setDisplayName(" 管理员 ");
        request.setEmail(" ADMIN@EXAMPLE.COM ");

        when(sysUserRepository.existsByUsername("admin_01")).thenReturn(false);
        when(sysUserRepository.save(any(SysUser.class))).thenAnswer(invocation -> {
            SysUser user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserVO result = userService.createUser(request);

        ArgumentCaptor<SysUser> captor = ArgumentCaptor.forClass(SysUser.class);
        verify(sysUserRepository).save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("admin_01");
        assertThat(captor.getValue().getDisplayName()).isEqualTo("管理员");
        assertThat(captor.getValue().getEmail()).isEqualTo("admin@example.com");
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldUpdateManagedUserWithoutCallingSave() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setDisplayName("旧名称");
        user.setStatus((short) 1);

        UpdateUserDTO request = new UpdateUserDTO();
        request.setDisplayName("新名称");
        request.setStatus((short) 0);

        when(sysUserRepository.findById(1L)).thenReturn(java.util.Optional.of(user));

        userService.updateUser(1L, request);

        assertThat(user.getDisplayName()).isEqualTo("新名称");
        assertThat(user.getStatus()).isZero();
        verify(sysUserRepository, never()).save(any(SysUser.class));
    }

    @Test
    void shouldDeleteUser() {
        SysUser user = new SysUser();
        user.setId(1L);

        when(sysUserRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        userService.deleteUser(1L);

        verify(sysUserRepository).delete(user);
    }
}
