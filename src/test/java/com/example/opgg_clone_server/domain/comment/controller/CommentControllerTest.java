package com.example.opgg_clone_server.domain.comment.controller;

import com.example.opgg_clone_server.domain.comment.Comment;
import com.example.opgg_clone_server.domain.comment.CommentException;
import com.example.opgg_clone_server.domain.comment.CommentExceptionType;
import com.example.opgg_clone_server.domain.comment.dto.CommentSaveDto;
import com.example.opgg_clone_server.domain.comment.repository.CommentRepository;
import com.example.opgg_clone_server.domain.comment.service.CommentService;
import com.example.opgg_clone_server.domain.member.Member;
import com.example.opgg_clone_server.domain.member.Role;
import com.example.opgg_clone_server.domain.member.repository.MemberRepository;
import com.example.opgg_clone_server.domain.member.service.MemberService;
import com.example.opgg_clone_server.domain.post.Post;
import com.example.opgg_clone_server.domain.post.dto.PostSaveDto;
import com.example.opgg_clone_server.domain.post.repository.PostRepository;
import com.example.opgg_clone_server.global.jwt.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    EntityManager em;
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentService commentService;
    @Autowired
    CommentRepository commentRepository;

    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    JwtService jwtService;

    final String USERNAME = "username1";

    private static Member member;


    @BeforeEach
    private void signUpAndSetAuthentication() throws Exception {
        member = memberRepository.save(Member.builder().username(USERNAME).password("1234567890").name("USER1").nickName("밥 잘먹는 동훈이1").role(Role.USER).age(22).build());
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username(USERNAME)
                                .password("1234567890")
                                .roles(Role.USER.toString())
                                .build(),
                        null)
        );
        SecurityContextHolder.setContext(emptyContext);
        clear();
    }


    private void clear() {
        em.flush();
        em.clear();
    }



    private String getAccessToken(){
        return jwtService.createAccessToken(USERNAME);
    }
    private String getNoAuthAccessToken(){
        return jwtService.createAccessToken(USERNAME+12);
    }

    private Long savePost(){
        String title = "제목";
        String content = "내용";
        PostSaveDto postSaveDto = new PostSaveDto(title, content, Optional.empty());


        //when
        Post save = postRepository.save(postSaveDto.toEntity());
        clear();
        return save.getId();
    }




    private Long saveComment(){
        CommentSaveDto commentSaveDto = new CommentSaveDto("댓글");
        commentService.save(savePost(),commentSaveDto);
        clear();

        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        return resultList.get(0).getId();
    }


    private Long saveReComment(Long parentId){

        CommentSaveDto commentSaveDto = new CommentSaveDto("대댓글");
        commentService.saveReComment(savePost(),parentId,commentSaveDto);
        clear();

        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        return resultList.get(0).getId();
    }


    @Test
    public void 댓글저장_성공() throws Exception {
        //given
        Long postId = savePost();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", "comment");


        //when
        mockMvc.perform(
                        post("/comment/"+postId)
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA).params(map))
                .andExpect(status().isCreated());



        //then
        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        assertThat(resultList.size()).isEqualTo(1);

    }



    @Test
    public void 대댓글저장_성공() throws Exception {

        //given
        Long postId = savePost();
        Long parentId = saveComment();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", "recomment");


        //when
        mockMvc.perform(
                        post("/comment/"+postId+"/"+parentId)
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA).params(map))
                .andExpect(status().isCreated());




        //then
        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        assertThat(resultList.size()).isEqualTo(2);

    }



    @Test
    public void 댓글저장_실패_게시물이_없음() throws Exception {
        //given
        Long postId = savePost();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", "comment");


        //when,then
        mockMvc.perform(
                        post("/comment/"+1000000)
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA).params(map))
                .andExpect(status().isNotFound());


    }

    @Test
    public void 대댓글저장_실패_게시물이_없음() throws Exception {
        //given
        Long postId = savePost();
        Long parentId = saveComment();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", "recomment");


        //when,then
        mockMvc.perform(
                        post("/comment/"+10000+"/"+parentId)
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA).params(map))
                .andExpect(status().isNotFound());
    }

    @Test
    public void 대댓글저장_실패_댓글이_없음() throws Exception {
        //given
        Long postId = savePost();
        Long parentId = saveComment();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", "recomment");


        //when,then
        mockMvc.perform(
                        post("/comment/"+postId+"/"+10000)
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA).params(map))
                .andExpect(status().isNotFound());
    }

    @Test
    public void 업데이트_성공() throws Exception {
        //given
        Long postId = savePost();
        Long commentId = saveComment();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", "updateComment");


        //when
        mockMvc.perform(
                        put("/comment/"+commentId)
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA).params(map))
                .andExpect(status().isOk());




        Comment comment = commentRepository.findById(commentId).orElse(null);
        assertThat(comment.getContent()).isEqualTo("updateComment");

    }



    @Test
    public void 업데이트_실패_권한이없음() throws Exception {
        //given
        Long postId = savePost();
        Long commentId = saveComment();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", "updateComment");


        //when
        mockMvc.perform(
                        put("/comment/"+commentId)
                                .header("Authorization", "Bearer "+ getNoAuthAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA).params(map))
                .andExpect(status().isForbidden());




        Comment comment = commentRepository.findById(commentId).orElse(null);
        assertThat(comment.getContent()).isEqualTo("댓글");
    }




    @Test
    public void 댓글삭제_실패_권한이_없음() throws Exception {
        //given
        Long postId = savePost();
        Long commentId = saveComment();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", "updateComment");


        //when
        mockMvc.perform(
                        delete("/comment/"+commentId)
                                .header("Authorization", "Bearer "+ getNoAuthAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA).params(map))
                .andExpect(status().isForbidden());




        Comment comment = commentRepository.findById(commentId).orElse(null);
        assertThat(comment.getContent()).isEqualTo("댓글");
    }




    // 댓글을 삭제하는 경우
    // 대댓글이 남아있는 경우
    // DB와 화면에서는 지워지지 않고, "삭제된 댓글입니다"라고 표시
    @Test
    public void 댓글삭제_대댓글이_남아있는_경우() throws Exception {

        //given
        Long commentId = saveComment();
        saveReComment(commentId);
        saveReComment(commentId);
        saveReComment(commentId);
        saveReComment(commentId);

        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).getChildList().size()).isEqualTo(4);

        //when
        mockMvc.perform(
                        delete("/comment/"+commentId)
                                .header("Authorization", "Bearer "+ getAccessToken()))
                .andExpect(status().isOk());


        //then
        Comment findComment = commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT));
        assertThat(findComment).isNotNull();
        assertThat(findComment.isRemoved()).isTrue();
        assertThat(findComment.getChildList().size()).isEqualTo(4);
    }




    // 댓글을 삭제하는 경우
    //대댓글이 아예 존재하지 않는 경우 : 곧바로 DB에서 삭제
    @Test
    public void 댓글삭제_대댓글이_없는_경우() throws Exception {
        //given
        Long commentId = saveComment();

        //when
        mockMvc.perform(
                        delete("/comment/"+commentId)
                                .header("Authorization", "Bearer "+ getAccessToken()))
                .andExpect(status().isOk());
        clear();

        //then
        Assertions.assertThat(commentRepository.findAll().size()).isSameAs(0);
        assertThat(assertThrows(CommentException.class, () ->commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).getExceptionType()).isEqualTo(CommentExceptionType.NOT_POUND_COMMENT);
    }




    // 댓글을 삭제하는 경우
    // 대댓글이 존재하나 모두 삭제된 경우
    //댓글과, 달려있는 대댓글 모두 DB에서 일괄 삭제, 화면상에도 표시되지 않음
    @Test
    public void 댓글삭제_대댓글이_존재하나_모두_삭제된_대댓글인_경우() throws Exception {
        //given
        Long commentId = saveComment();
        Long reCommend1Id = saveReComment(commentId);
        Long reCommend2Id = saveReComment(commentId);
        Long reCommend3Id = saveReComment(commentId);
        Long reCommend4Id = saveReComment(commentId);


        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).getChildList().size()).isEqualTo(4);
        clear();

        commentService.remove(reCommend1Id);
        clear();

        commentService.remove(reCommend2Id);
        clear();

        commentService.remove(reCommend3Id);
        clear();

        commentService.remove(reCommend4Id);
        clear();


        Assertions.assertThat(commentRepository.findById(reCommend1Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).isRemoved()).isTrue();
        Assertions.assertThat(commentRepository.findById(reCommend2Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).isRemoved()).isTrue();
        Assertions.assertThat(commentRepository.findById(reCommend3Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).isRemoved()).isTrue();
        Assertions.assertThat(commentRepository.findById(reCommend4Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).isRemoved()).isTrue();
        clear();


        //when
        mockMvc.perform(
                        delete("/comment/"+commentId)
                                .header("Authorization", "Bearer "+ getAccessToken()))
                .andExpect(status().isOk());
        clear();


        //then
        LongStream.rangeClosed(commentId, reCommend4Id).forEach(id ->
                assertThat(assertThrows(CommentException.class, () -> commentRepository.findById(id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).getExceptionType()).isEqualTo(CommentExceptionType.NOT_POUND_COMMENT)
        );

    }





    // 대댓글을 삭제하는 경우
    // 부모 댓글이 삭제되지 않은 경우
    // 내용만 삭제, DB에서는 삭제 X
    @Test
    public void 대댓글삭제_부모댓글이_남아있는_경우() throws Exception {
        //given
        Long commentId = saveComment();
        Long reCommend1Id = saveReComment(commentId);


        //when
        mockMvc.perform(
                        delete("/comment/"+reCommend1Id)
                                .header("Authorization", "Bearer "+ getAccessToken()))
                .andExpect(status().isOk());
        clear();


        //then
        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).isNotNull();
        Assertions.assertThat(commentRepository.findById(reCommend1Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).isNotNull();
        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).isRemoved()).isFalse();
        Assertions.assertThat(commentRepository.findById(reCommend1Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).isRemoved()).isTrue();
    }



    // 대댓글을 삭제하는 경우
    // 부모 댓글이 삭제되어있고, 대댓글들도 모두 삭제된 경우
    // 부모를 포함한 모든 대댓글을 DB에서 일괄 삭제, 화면상에서도 지움
    @Test
    public void 대댓글삭제_부모댓글이_삭제된_경우_모든_대댓글이_삭제된_경우() throws Exception {
        //given
        Long commentId = saveComment();
        Long reCommend1Id = saveReComment(commentId);
        Long reCommend2Id = saveReComment(commentId);
        Long reCommend3Id = saveReComment(commentId);


        commentService.remove(reCommend2Id);
        clear();
        commentService.remove(commentId);
        clear();
        commentService.remove(reCommend3Id);
        clear();


        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).isNotNull();
        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).getChildList().size()).isEqualTo(3);

        //when
        mockMvc.perform(
                        delete("/comment/"+reCommend1Id)
                                .header("Authorization", "Bearer "+ getAccessToken()))
                .andExpect(status().isOk());



        //then
        LongStream.rangeClosed(commentId, reCommend3Id).forEach(id ->
                assertThat(assertThrows(CommentException.class, () -> commentRepository.findById(id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).getExceptionType()).isEqualTo(CommentExceptionType.NOT_POUND_COMMENT)
        );



    }


    // 대댓글을 삭제하는 경우
    // 부모 댓글이 삭제되어있고, 다른 대댓글이 아직 삭제되지 않고 남아있는 경우
    //해당 대댓글만 삭제, 그러나 DB에서 삭제되지는 않고, 화면상에는 "삭제된 댓글입니다"라고 표시
    @Test
    public void 대댓글삭제_부모댓글이_삭제된_경우_다른_대댓글이_남아있는_경우() throws Exception {
        //given
        Long commentId = saveComment();
        Long reCommend1Id = saveReComment(commentId);
        Long reCommend2Id = saveReComment(commentId);
        Long reCommend3Id = saveReComment(commentId);


        commentService.remove(reCommend3Id);
        commentService.remove(commentId);
        clear();

        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).isNotNull();
        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).getChildList().size()).isEqualTo(3);


        //when
        mockMvc.perform(
                        delete("/comment/"+reCommend2Id)
                                .header("Authorization", "Bearer "+ getAccessToken()))
                .andExpect(status().isOk());


        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).isNotNull();


        //then
        Assertions.assertThat(commentRepository.findById(reCommend2Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).isNotNull();
        Assertions.assertThat(commentRepository.findById(reCommend2Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).isRemoved()).isTrue();
        Assertions.assertThat(commentRepository.findById(reCommend1Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).getId()).isNotNull();
        Assertions.assertThat(commentRepository.findById(reCommend3Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).getId()).isNotNull();
        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).getId()).isNotNull();

    }
}