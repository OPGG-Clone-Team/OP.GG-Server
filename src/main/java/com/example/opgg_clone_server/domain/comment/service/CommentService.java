package com.example.opgg_clone_server.domain.comment.service;

import com.example.opgg_clone_server.domain.comment.Comment;
import com.example.opgg_clone_server.domain.comment.CommentException;
import com.example.opgg_clone_server.domain.comment.dto.CommentSaveDto;
import com.example.opgg_clone_server.domain.comment.dto.CommentUpdateDto;

import java.util.List;

public interface CommentService {

    void save(Long postId , CommentSaveDto commentSaveDto);
    void saveReComment(Long postId, Long parentId , CommentSaveDto commentSaveDto);

    void update(Long id, CommentUpdateDto commentUpdateDto);

    void remove(Long id) throws CommentException;
}
