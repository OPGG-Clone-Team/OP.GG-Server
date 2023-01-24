package com.example.opgg_clone_server.global.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    // 공통예외처리
    @ExceptionHandler(BaseException.class)
    public ResponseEntity handleBaseEx(BaseException exception){
        log.error("BaseException errorMessage(): {}",exception.getExceptionType().getErrorMessage());
        log.error("BaseException errorCode(): {}",exception.getExceptionType().getErrorCode());

        return new ResponseEntity(new ExceptionDto(exception.getExceptionType().getErrorCode()),exception.getExceptionType().getHttpStatus());
    }

    // MemberService에서 오류 났을 때 예외처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleMemberEx(Exception exception){
        exception.printStackTrace();
        return new ResponseEntity(HttpStatus.OK);
    }

    @Data
    @AllArgsConstructor
    static class ExceptionDto {
        private Integer errorCode;
    }
}