package com.book.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 借阅记录实体，对应 borrow 表。
 * userName、bookName 来自关联查询，仅用于页面展示。
 */
@Data
public class Borrow {
    private Integer id;
    private Integer userId;
    private Integer bookId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date borrowTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date returnTime;

    private String userName;
    private String bookName;
}
