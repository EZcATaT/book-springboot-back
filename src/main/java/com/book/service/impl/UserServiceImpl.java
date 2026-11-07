package com.book.service.impl;

import com.book.common.BusinessException;
import com.book.entity.User;
import com.book.mapper.BorrowMapper;
import com.book.mapper.UserMapper;
import com.book.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    public static final String ADMIN_USERNAME = "admin";

    private final UserMapper userMapper;
    private final BorrowMapper borrowMapper;

    public UserServiceImpl(UserMapper userMapper, BorrowMapper borrowMapper) {
        this.userMapper = userMapper;
        this.borrowMapper = borrowMapper;
    }

    @Override
    public List<User> findAll() {
        List<User> users = userMapper.selectAll();
        users.forEach(this::fillRole);
        return users;
    }

    @Override
    public User findById(Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return fillRole(user);
    }

    @Override
    public User login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new BusinessException("用户名和密码不能为空");
        }
        User user = userMapper.selectByUsername(username.trim());
        if (user == null || !password.equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        return fillRole(user);
    }

    @Override
    public int add(User user) {
        if (user == null) {
            throw new BusinessException("用户信息不能为空");
        }
        normalize(user);
        if (!StringUtils.hasText(user.getUsername())
                || !StringUtils.hasText(user.getPassword())
                || !StringUtils.hasText(user.getName())) {
            throw new BusinessException("用户名、密码和姓名不能为空");
        }
        if (ADMIN_USERNAME.equals(user.getUsername())) {
            throw new BusinessException("admin 为系统保留账号，不能注册或新增");
        }
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        return userMapper.insert(user);
    }

    @Override
    public int update(User user) {
        if (user == null || user.getId() == null) {
            throw new BusinessException("用户 id 不能为空");
        }
        User existing = userMapper.selectById(user.getId());
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        normalize(user);
        if (!StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getName())) {
            throw new BusinessException("用户名和姓名不能为空");
        }
        if (ADMIN_USERNAME.equals(existing.getUsername())
                && !ADMIN_USERNAME.equals(user.getUsername())) {
            throw new BusinessException("默认管理员账号的用户名不能修改");
        }
        User sameName = userMapper.selectByUsername(user.getUsername());
        if (sameName != null && !sameName.getId().equals(user.getId())) {
            throw new BusinessException("用户名已存在");
        }
        // 密码留空表示保持原密码不变
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword(existing.getPassword());
        }
        return userMapper.update(user);
    }

    @Override
    public int delete(Integer id) {
        User existing = userMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        if (ADMIN_USERNAME.equals(existing.getUsername())) {
            throw new BusinessException("默认管理员账号不能删除");
        }
        if (borrowMapper.countByUserId(id) > 0) {
            throw new BusinessException("该用户存在借阅记录，请先删除相关借阅记录");
        }
        return userMapper.deleteById(id);
    }

    private User fillRole(User user) {
        user.setRole(ADMIN_USERNAME.equals(user.getUsername()) ? "admin" : "user");
        return user;
    }

    private void normalize(User user) {
        user.setUsername(trim(user.getUsername()));
        user.setName(trim(user.getName()));
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
