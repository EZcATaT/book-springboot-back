package com.book.common;

/**
 * 可预期的业务异常，统一返回 code=500。
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
