package com.tukeping.exception;

/**
 * @author tukeping
 * @date 2019/11/28
 **/
public class DuplicateRecordException extends RuntimeException {

    public DuplicateRecordException(String message) {
        super(message);
    }
}
