package com.book.service;

import com.book.entity.Borrow;

import java.util.List;

public interface BorrowService {
    List<Borrow> findAll();

    List<Borrow> findByUserId(Integer userId);

    Borrow findById(Integer id);

    int add(Borrow borrow);

    int update(Borrow borrow);

    int delete(Integer id);
}
