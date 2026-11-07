package com.book.controller;

import com.book.auth.AuthUtil;
import com.book.auth.SessionUser;
import com.book.common.Result;
import com.book.entity.Borrow;
import com.book.service.BorrowService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 借阅模块接口。
 * 主要使用 /api/borrows、/api/users/{userId}/borrows 等 RESTful 路径，
 * 同时兼容 /borrow/list 等旧路径。
 */
@RestController
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @GetMapping({"/borrow/list", "/api/borrows"})
    public Result<List<Borrow>> list() {
        AuthUtil.requireAdmin();
        return Result.success(borrowService.findAll());
    }

    @GetMapping({"/borrow/user/{userId}", "/api/users/{userId}/borrows"})
    public Result<List<Borrow>> listByUserId(@PathVariable Integer userId) {
        AuthUtil.requireSelfOrAdmin(userId);
        return Result.success(borrowService.findByUserId(userId));
    }

    @GetMapping({"/borrow/{id}", "/api/borrows/{id}"})
    public Result<Borrow> getById(@PathVariable Integer id) {
        Borrow borrow = borrowService.findById(id);
        AuthUtil.requireSelfOrAdmin(borrow.getUserId());
        return Result.success(borrow);
    }

    @PostMapping({"/borrow/add", "/api/borrows"})
    public Result<Void> add(@RequestBody Borrow borrow) {
        SessionUser current = AuthUtil.currentUser();
        if (!"admin".equals(current.role())) {
            // 普通用户只能给自己借书，不能伪造 userId
            borrow.setUserId(current.id());
        }
        borrowService.add(borrow);
        return Result.successMsg("借阅成功");
    }

    @PutMapping({"/borrow/update", "/api/borrows/{id}"})
    public Result<Void> update(@PathVariable(required = false) Integer id,
                               @RequestBody Borrow borrow) {
        if (id != null) {
            borrow.setId(id);
        }
        Borrow existing = borrowService.findById(borrow.getId());
        AuthUtil.requireSelfOrAdmin(existing.getUserId());
        borrowService.update(borrow);
        return Result.successMsg("归还成功");
    }

    @DeleteMapping({"/borrow/{id}", "/api/borrows/{id}"})
    public Result<Void> delete(@PathVariable Integer id) {
        AuthUtil.requireAdmin();
        borrowService.delete(id);
        return Result.successMsg("删除借阅记录成功");
    }
}
