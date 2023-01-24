package com.example.opgg_clone_server.domain.member.exception;

import com.example.opgg_clone_server.global.exception.BaseException;
import com.example.opgg_clone_server.global.exception.BaseExceptionType;

public class MemberException extends BaseException {
    private BaseExceptionType exceptionType;


    public MemberException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
