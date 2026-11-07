package com.book.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 用户实体，对应 user 表：id、username、password、name。
 * role 不是数据库字段，由用户名推导（admin 为管理员，其余为普通用户）。
 */
@Data
public class User {
    private Integer id;
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String name;
    private String role;
}
