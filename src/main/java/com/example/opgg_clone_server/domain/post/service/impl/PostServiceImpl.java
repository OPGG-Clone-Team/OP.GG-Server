package com.example.opgg_clone_server.domain.post.service.impl;

import com.example.opgg_clone_server.domain.member.exception.MemberException;
import com.example.opgg_clone_server.domain.member.exception.MemberExceptionType;
import com.example.opgg_clone_server.domain.member.repository.MemberRepository;
import com.example.opgg_clone_server.domain.post.Post;
import com.example.opgg_clone_server.domain.post.cond.PostSearchCondition;
import com.example.opgg_clone_server.domain.post.dto.PostInfoDto;
import com.example.opgg_clone_server.domain.post.dto.PostPagingDto;
import com.example.opgg_clone_server.domain.post.dto.PostSaveDto;
import com.example.opgg_clone_server.domain.post.dto.PostUpdateDto;
import com.example.opgg_clone_server.domain.post.exception.PostException;
import com.example.opgg_clone_server.domain.post.exception.PostExceptionType;
import com.example.opgg_clone_server.domain.post.repository.PostRepository;
import com.example.opgg_clone_server.domain.post.service.PostService;
import com.example.opgg_clone_server.global.file.exception.FileException;
import com.example.opgg_clone_server.global.file.service.FileService;
import com.example.opgg_clone_server.global.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.awt.print.Pageable;

import static com.example.opgg_clone_server.domain.post.exception.PostExceptionType.POST_NOT_POUND;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    @Override
    public void save(PostSaveDto postSaveDto) throws FileException {
        Post post = postSaveDto.toEntity();

        post.confirmWriter(memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)));

        postSaveDto.uploadFile().ifPresent(
                file ->  post.updateFilePath(fileService.save(file))
        );
        
        postRepository.save(post);
    }



    @Override
    public void update(Long id, PostUpdateDto postUpdateDto) {

        Post post = postRepository.findById(id).orElseThrow(() ->
                new PostException(POST_NOT_POUND));

        checkAuthority(post,PostExceptionType.NOT_AUTHORITY_UPDATE_POST );

        postUpdateDto.title().ifPresent(post::updateTitle);
        postUpdateDto.content().ifPresent(post::updateContent);


        if(post.getFilePath() !=null){
            fileService.delete(post.getFilePath());//기존에 올린 파일 지우기
        }
        
        postUpdateDto.uploadFile().ifPresentOrElse(
                multipartFile ->  post.updateFilePath(fileService.save(multipartFile)),
                () ->  post.updateFilePath(null)
                );
    }


    @Override
    public void delete(Long id) {

        Post post = postRepository.findById(id).orElseThrow(() ->
                new PostException(POST_NOT_POUND));

        checkAuthority(post,PostExceptionType.NOT_AUTHORITY_DELETE_POST);


        if(post.getFilePath() !=null){
            fileService.delete(post.getFilePath());//기존에 올린 파일 지우기
        }

        postRepository.delete(post);

    }


    private void checkAuthority(Post post, PostExceptionType postExceptionType) {
        if(!post.getWriter().getUsername().equals(SecurityUtil.getLoginUsername()))
            throw new PostException(postExceptionType);
    }


    @Override
    public PostInfoDto getPostInfo(Long id) {
        return new PostInfoDto(postRepository.findWithWriterById(id)
                .orElseThrow(() -> new PostException(POST_NOT_POUND)));
    }

    @Override
    public PostPagingDto getPostList(Pageable pageable, PostSearchCondition postSearchCondition) {
        return null;
    }
}