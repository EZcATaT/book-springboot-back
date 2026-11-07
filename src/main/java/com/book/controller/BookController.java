package com.book.controller;

import com.book.auth.AuthUtil;
import com.book.common.Result;
import com.book.entity.Book;
import com.book.service.BookService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 图书模块接口。
 * 主要使用 /api/books 等 RESTful 路径，同时兼容 /book/list 等旧路径。
 */
@RestController
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping({"/book/list", "/api/books"})
    public Result<List<Book>> list() {
        return Result.success(bookService.findAll());
    }

    @GetMapping({"/book/{id}", "/api/books/{id}"})
    public Result<Book> getById(@PathVariable Integer id) {
        return Result.success(bookService.findById(id));
    }

    @PostMapping({"/book/add", "/api/books"})
    public Result<Void> add(@RequestBody Book book) {
        AuthUtil.requireAdmin();
        bookService.add(book);
        return Result.successMsg("新增图书成功");
    }

    @PutMapping({"/book/update", "/api/books/{id}"})
    public Result<Void> update(@PathVariable(required = false) Integer id,
                               @RequestBody Book book) {
        AuthUtil.requireAdmin();
        if (id != null) {
            book.setId(id);
        }
        bookService.update(book);
        return Result.successMsg("修改图书成功");
    }

    @DeleteMapping({"/book/{id}", "/api/books/{id}"})
    public Result<Void> delete(@PathVariable Integer id) {
        AuthUtil.requireAdmin();
        bookService.delete(id);
        return Result.successMsg("删除图书成功");
    }
}
