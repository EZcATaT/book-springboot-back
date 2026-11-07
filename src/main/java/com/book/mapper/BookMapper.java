package com.book.mapper;

import com.book.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookMapper {
    List<Book> selectAll();

    Book selectById(Integer id);

    Book selectByIsbn(String isbn);

    int insert(Book book);

    int update(Book book);

    int deleteById(Integer id);

    int decreaseStock(@Param("id") Integer id);

    int increaseStock(@Param("id") Integer id);
}
