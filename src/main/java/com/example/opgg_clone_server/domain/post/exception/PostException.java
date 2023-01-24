package com.example.opgg_clone_server.domain.post.exception;

import com.example.opgg_clone_server.global.exception.BaseException;
import com.example.opgg_clone_server.global.exception.BaseExceptionType;

public class PostException extends BaseException {

    private BaseExceptionType baseExceptionType;


    public PostException(BaseExceptionType baseExceptionType) {
        this.baseExceptionType = baseExceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return this.baseExceptionType;
    }
}
