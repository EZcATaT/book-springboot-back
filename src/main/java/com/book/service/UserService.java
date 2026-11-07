package com.book.service;

import com.book.entity.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User findById(Integer id);

    User login(String username, String password);

    int add(User user);

    int update(User user);

    int delete(Integer id);
}
