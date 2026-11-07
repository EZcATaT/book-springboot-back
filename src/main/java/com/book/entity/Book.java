package com.book.entity;

import lombok.Data;

/**
 * 图书实体，对应 book 表：id、book_name、author、isbn、stock。
 */
@Data
public class Book {
    private Integer id;
    private String bookName;
    private String author;
    private String isbn;
    private Integer stock;
}
