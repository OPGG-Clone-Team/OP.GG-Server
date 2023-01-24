package com.example.opgg_clone_server.domain.comment;


import com.example.opgg_clone_server.global.exception.BaseException;
import com.example.opgg_clone_server.global.exception.BaseExceptionType;

public class CommentException extends BaseException {

    private BaseExceptionType baseExceptionType;


    public CommentException(BaseExceptionType baseExceptionType) {
        this.baseExceptionType = baseExceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return this.baseExceptionType;
    }
}