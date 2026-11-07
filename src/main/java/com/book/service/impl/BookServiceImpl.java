package com.book.service.impl;

import com.book.common.BusinessException;
import com.book.entity.Book;
import com.book.mapper.BookMapper;
import com.book.mapper.BorrowMapper;
import com.book.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final BorrowMapper borrowMapper;

    public BookServiceImpl(BookMapper bookMapper, BorrowMapper borrowMapper) {
        this.bookMapper = bookMapper;
        this.borrowMapper = borrowMapper;
    }

    @Override
    public List<Book> findAll() {
        return bookMapper.selectAll();
    }

    @Override
    public Book findById(Integer id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        return book;
    }

    @Override
    public int add(Book book) {
        if (book == null) {
            throw new BusinessException("图书信息不能为空");
        }
        normalize(book);
        validate(book);
        if (bookMapper.selectByIsbn(book.getIsbn()) != null) {
            throw new BusinessException("ISBN 已存在");
        }
        return bookMapper.insert(book);
    }

    @Override
    public int update(Book book) {
        if (book == null || book.getId() == null) {
            throw new BusinessException("图书 id 不能为空");
        }
        if (bookMapper.selectById(book.getId()) == null) {
            throw new BusinessException("图书不存在");
        }
        normalize(book);
        validate(book);
        Book sameIsbn = bookMapper.selectByIsbn(book.getIsbn());
        if (sameIsbn != null && !sameIsbn.getId().equals(book.getId())) {
            throw new BusinessException("ISBN 已存在");
        }
        return bookMapper.update(book);
    }

    @Override
    public int delete(Integer id) {
        if (bookMapper.selectById(id) == null) {
            throw new BusinessException("图书不存在");
        }
        if (borrowMapper.countByBookId(id) > 0) {
            throw new BusinessException("该图书存在借阅记录，请先删除相关借阅记录");
        }
        return bookMapper.deleteById(id);
    }

    private void normalize(Book book) {
        book.setBookName(trim(book.getBookName()));
        book.setAuthor(trim(book.getAuthor()));
        book.setIsbn(trim(book.getIsbn()));
    }

    private void validate(Book book) {
        if (!StringUtils.hasText(book.getBookName())
                || !StringUtils.hasText(book.getAuthor())
                || !StringUtils.hasText(book.getIsbn())) {
            throw new BusinessException("书名、作者和 ISBN 不能为空");
        }
        if (book.getStock() == null) {
            book.setStock(0);
        }
        if (book.getStock() < 0) {
            throw new BusinessException("库存不能小于 0");
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
