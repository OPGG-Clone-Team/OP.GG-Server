package com.example.opgg_clone_server.global.file.exception;

import com.example.opgg_clone_server.global.exception.BaseException;
import com.example.opgg_clone_server.global.exception.BaseExceptionType;

public class FileException extends BaseException {
    private BaseExceptionType exceptionType;


    public FileException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
