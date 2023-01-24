package com.example.opgg_clone_server.global.log;

public interface LogTrace {

    TraceStatus begin(String message);

    void end(TraceStatus status);
    void exception(TraceStatus status, Throwable e);
}