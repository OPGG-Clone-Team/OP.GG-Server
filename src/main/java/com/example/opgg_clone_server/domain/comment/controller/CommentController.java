package com.example.opgg_clone_server.domain.comment.controller;

import com.example.opgg_clone_server.domain.comment.dto.CommentSaveDto;
import com.example.opgg_clone_server.domain.comment.dto.CommentUpdateDto;
import com.example.opgg_clone_server.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {
    /**
     * 댓글을 따로 불러오는 기능을 Repository에 만들지 않았기 때문에 GetMapping은 별도로 사용하지 않음
     */
    private final CommentService commentService;

    @PostMapping("/comment/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void commentSave(@PathVariable("postId") Long postId, CommentSaveDto commentSaveDto){
        commentService.save(postId, commentSaveDto);
    }


    @PostMapping("/comment/{postId}/{commentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void reCommentSave(@PathVariable("postId") Long postId,
                              @PathVariable("commentId") Long commentId,
                              CommentSaveDto commentSaveDto){
        commentService.saveReComment(postId, commentId, commentSaveDto);
    }


    @PutMapping("/comment/{commentId}")
    public void update(@PathVariable("commentId") Long commentId,
                              CommentUpdateDto commentUpdateDto){
        commentService.update(commentId, commentUpdateDto);
    }


    @DeleteMapping("/comment/{commentId}")
    public void delete(@PathVariable("commentId") Long commentId){
        commentService.remove(commentId);
    }
}