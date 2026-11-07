package com.book.service;

import com.book.entity.Book;

import java.util.List;

public interface BookService {
    List<Book> findAll();

    Book findById(Integer id);

    int add(Book book);

    int update(Book book);

    int delete(Integer id);
}
