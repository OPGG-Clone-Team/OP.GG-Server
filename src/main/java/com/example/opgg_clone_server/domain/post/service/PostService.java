package com.example.opgg_clone_server.domain.post.service;

import com.example.opgg_clone_server.domain.post.cond.PostSearchCondition;
import com.example.opgg_clone_server.domain.post.dto.PostInfoDto;
import com.example.opgg_clone_server.domain.post.dto.PostPagingDto;
import com.example.opgg_clone_server.domain.post.dto.PostSaveDto;
import com.example.opgg_clone_server.domain.post.dto.PostUpdateDto;
import com.example.opgg_clone_server.global.file.exception.FileException;

import java.awt.print.Pageable;

public interface PostService {

    /**
     * 게시글 등록
     */
    void save(PostSaveDto postSaveDto) throws FileException;

    /**
     * 게시글 수정
     */
    void update(Long id, PostUpdateDto postUpdateDto);

    /**
     * 게시글 삭제
     */
    void delete(Long id);

    /**
     * 게시글 1개 조회
     */
    PostInfoDto getPostInfo(Long id);


    /**
     * 검색 조건에 따른 게시글 리스트 조회 + 페이징
     */
    PostPagingDto getPostList(Pageable pageable, PostSearchCondition postSearchCondition);
}