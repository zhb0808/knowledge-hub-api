package com.software.knowledgehub.system.service;

import com.software.knowledgehub.system.dto.CreateUserDTO;
import com.software.knowledgehub.system.dto.UpdateUserDTO;
import com.software.knowledgehub.system.vo.UserVO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface UserService {

    /**
     * 创建用户。
     */
    UserVO createUser(CreateUserDTO request);

    /**
     * 查询用户详情。
     */
    UserVO getUser(Long id);

    /**
     * 分页查询用户。
     */
    Page<UserVO> listUsers(Pageable pageable);

    /**
     * 修改用户资料和状态。
     */
    void updateUser(Long id, UpdateUserDTO request);

    /**
     * 删除用户。
     */
    void deleteUser(Long id);
}
