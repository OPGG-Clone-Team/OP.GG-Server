package com.example.opgg_clone_server.domain.comment.dto;

import com.example.opgg_clone_server.domain.comment.Comment;

public record CommentSaveDto (String content){

    public Comment toEntity() {
        return Comment.builder().content(content).build();
    }
}
