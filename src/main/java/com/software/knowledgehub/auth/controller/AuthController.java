package com.software.knowledgehub.auth.controller;

import com.software.knowledgehub.auth.dto.LoginDTO;
import com.software.knowledgehub.auth.service.AuthService;
import com.software.knowledgehub.auth.vo.CurrentUserVO;
import com.software.knowledgehub.auth.vo.LoginVO;
import com.software.knowledgehub.common.response.ApiResponse;
import com.software.knowledgehub.security.model.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginDTO request) {
        return ApiResponse.success(authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserVO> getCurrentUser(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.success(authService.getCurrentUser(user));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal AuthenticatedUser user) {
        authService.logout(user);
        return ApiResponse.success(null);
    }
}
