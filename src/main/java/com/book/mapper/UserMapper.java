package com.book.mapper;

import com.book.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> selectAll();

    User selectById(Integer id);

    User selectByUsername(String username);

    int insert(User user);

    int update(User user);

    int deleteById(Integer id);
}
