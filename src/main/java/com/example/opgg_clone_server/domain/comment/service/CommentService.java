package com.example.opgg_clone_server.domain.comment.service;

import com.example.opgg_clone_server.domain.comment.Comment;

import java.util.List;

public interface CommentService {

    void save(Comment comment);

    Comment findById(Long id) throws Exception;


    List<Comment> findAll();

    void remove(Long id) throws Exception;
}
