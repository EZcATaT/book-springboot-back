package com.book.service.impl;

import com.book.common.BusinessException;
import com.book.entity.Book;
import com.book.entity.Borrow;
import com.book.entity.User;
import com.book.mapper.BookMapper;
import com.book.mapper.BorrowMapper;
import com.book.mapper.UserMapper;
import com.book.service.BorrowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class BorrowServiceImpl implements BorrowService {

    private final BorrowMapper borrowMapper;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    public BorrowServiceImpl(BorrowMapper borrowMapper,
                             BookMapper bookMapper,
                             UserMapper userMapper) {
        this.borrowMapper = borrowMapper;
        this.bookMapper = bookMapper;
        this.userMapper = userMapper;
    }

    @Override
    public List<Borrow> findAll() {
        return borrowMapper.selectAll();
    }

    @Override
    public List<Borrow> findByUserId(Integer userId) {
        if (userMapper.selectById(userId) == null) {
            throw new BusinessException("用户不存在");
        }
        return borrowMapper.selectByUserId(userId);
    }

    @Override
    public Borrow findById(Integer id) {
        Borrow borrow = borrowMapper.selectById(id);
        if (borrow == null) {
            throw new BusinessException("借阅记录不存在");
        }
        return borrow;
    }

    @Override
    @Transactional
    public int add(Borrow borrow) {
        if (borrow == null || borrow.getUserId() == null || borrow.getBookId() == null) {
            throw new BusinessException("用户 id 和图书 id 不能为空");
        }
        User user = userMapper.selectById(borrow.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        Book book = bookMapper.selectById(borrow.getBookId());
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        if (book.getStock() == null || book.getStock() <= 0) {
            throw new BusinessException("该图书库存不足，暂时无法借阅");
        }
        if (borrowMapper.selectActiveByUserAndBook(borrow.getUserId(), borrow.getBookId()) != null) {
            throw new BusinessException("该用户已借阅这本书且尚未归还");
        }
        borrow.setBorrowTime(borrow.getBorrowTime() == null ? new Date() : borrow.getBorrowTime());
        borrow.setReturnTime(null);

        // 乐观扣减库存，避免库存被扣成负数
        if (bookMapper.decreaseStock(book.getId()) == 0) {
            throw new BusinessException("该图书库存不足，暂时无法借阅");
        }
        return borrowMapper.insert(borrow);
    }

    @Override
    @Transactional
    public int update(Borrow borrow) {
        if (borrow == null || borrow.getId() == null) {
            throw new BusinessException("借阅记录 id 不能为空");
        }
        Borrow existing = borrowMapper.selectById(borrow.getId());
        if (existing == null) {
            throw new BusinessException("借阅记录不存在");
        }
        if (existing.getReturnTime() != null) {
            throw new BusinessException("该借阅记录已经归还，无需重复操作");
        }

        Date returnTime = borrow.getReturnTime() == null ? new Date() : borrow.getReturnTime();
        if (existing.getBorrowTime() != null && returnTime.before(existing.getBorrowTime())) {
            throw new BusinessException("归还时间不能早于借阅时间");
        }
        int rows = borrowMapper.updateReturnTime(existing.getId(), returnTime);
        if (rows > 0) {
            bookMapper.increaseStock(existing.getBookId());
        }
        return rows;
    }

    @Override
    @Transactional
    public int delete(Integer id) {
        Borrow existing = borrowMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("借阅记录不存在");
        }
        int rows = borrowMapper.deleteById(id);
        // 删除未归还记录时，把库存补回去，保证数据一致
        if (rows > 0 && existing.getReturnTime() == null) {
            bookMapper.increaseStock(existing.getBookId());
        }
        return rows;
    }
}
