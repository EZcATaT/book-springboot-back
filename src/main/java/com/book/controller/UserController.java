package com.book.controller;

import com.book.auth.AuthUtil;
import com.book.auth.TokenStore;
import com.book.common.Result;
import com.book.dto.LoginRequest;
import com.book.dto.LoginResponse;
import com.book.entity.User;
import com.book.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户模块接口。
 * 主要使用 /api/users 等 RESTful 路径；
 * 同时保留 /user/list、/user/add 等旧路径，兼容最初的接口清单。
 */
@RestController
public class UserController {

    private final UserService userService;
    private final TokenStore tokenStore;

    public UserController(UserService userService, TokenStore tokenStore) {
        this.userService = userService;
        this.tokenStore = tokenStore;
    }

    @GetMapping({"/user/list", "/api/users"})
    public Result<List<User>> list() {
        AuthUtil.requireAdmin();
        return Result.success(userService.findAll());
    }

    @GetMapping({"/user/{id}", "/api/users/{id}"})
    public Result<User> getById(@PathVariable Integer id) {
        AuthUtil.requireAdmin();
        return Result.success(userService.findById(id));
    }

    @PostMapping({"/user/add", "/api/users"})
    public Result<Void> add(@RequestBody User user) {
        AuthUtil.requireAdmin();
        userService.add(user);
        return Result.successMsg("新增用户成功");
    }

    @PutMapping({"/user/update", "/api/users/{id}"})
    public Result<Void> update(@PathVariable(required = false) Integer id,
                               @RequestBody User user) {
        AuthUtil.requireAdmin();
        if (id != null) {
            user.setId(id);
        }
        userService.update(user);
        return Result.successMsg("修改用户成功");
    }

    @DeleteMapping({"/user/{id}", "/api/users/{id}"})
    public Result<Void> delete(@PathVariable Integer id) {
        AuthUtil.requireAdmin();
        userService.delete(id);
        return Result.successMsg("删除用户成功");
    }

    /**
     * 普通用户自助注册，注册后的账号固定为普通用户。
     */
    @PostMapping({"/user/register", "/api/auth/register"})
    public Result<Void> register(@RequestBody User user) {
        userService.add(user);
        return Result.successMsg("注册成功，请登录");
    }

    @PostMapping({"/user/login", "/api/auth/login"})
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        String token = tokenStore.create(user);
        return Result.success("登录成功", new LoginResponse(token, user));
    }

    @PostMapping({"/user/logout", "/api/auth/logout"})
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        tokenStore.remove(authorization);
        return Result.successMsg("已退出登录");
    }
}
