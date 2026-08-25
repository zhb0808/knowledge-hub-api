package com.software.knowledgehub.system.controller;

import com.software.knowledgehub.common.response.ApiResponse;
import com.software.knowledgehub.system.dto.CreateUserDTO;
import com.software.knowledgehub.system.dto.UpdateUserDTO;
import com.software.knowledgehub.system.service.UserService;
import com.software.knowledgehub.system.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ApiResponse<UserVO> createUser(@Valid @RequestBody CreateUserDTO request) {
        return ApiResponse.success(userService.createUser(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserVO> getUser(@PathVariable Long id) {
        return ApiResponse.success(userService.getUser(id));
    }

    @GetMapping
    public ApiResponse<Page<UserVO>> listUsers(
            @PageableDefault(
                    size = 10,
                    sort = "createdTime",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        return ApiResponse.success(userService.listUsers(pageable));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDTO request) {
        userService.updateUser(id, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success(null);
    }
}
