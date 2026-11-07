package com.book.mapper;

import com.book.entity.Borrow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface BorrowMapper {
    List<Borrow> selectAll();

    List<Borrow> selectByUserId(Integer userId);

    Borrow selectById(Integer id);

    Borrow selectActiveByUserAndBook(@Param("userId") Integer userId,
                                     @Param("bookId") Integer bookId);

    int countByUserId(Integer userId);

    int countByBookId(Integer bookId);

    int insert(Borrow borrow);

    int updateReturnTime(@Param("id") Integer id,
                         @Param("returnTime") Date returnTime);

    int deleteById(Integer id);
}
