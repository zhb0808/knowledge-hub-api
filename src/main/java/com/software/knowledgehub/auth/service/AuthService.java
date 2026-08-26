package com.software.knowledgehub.auth.service;

import com.software.knowledgehub.auth.dto.LoginDTO;
import com.software.knowledgehub.auth.vo.CurrentUserVO;
import com.software.knowledgehub.auth.vo.LoginVO;
import com.software.knowledgehub.security.model.AuthenticatedUser;

public interface AuthService {

    /**
     * 校验账号密码并创建登录状态。
     */
    LoginVO login(LoginDTO request);

    /**
     * 查询当前登录用户信息。
     */
    CurrentUserVO getCurrentUser(AuthenticatedUser user);

    /**
     * 退出当前登录状态。
     */
    void logout(AuthenticatedUser user);
}
