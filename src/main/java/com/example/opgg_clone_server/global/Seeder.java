//package com.example.opgg_clone_server.global;
//
//import com.example.opgg_clone_server.domain.comment.Comment;
//import com.example.opgg_clone_server.domain.comment.repository.CommentRepository;
//import com.example.opgg_clone_server.domain.member.Member;
//import com.example.opgg_clone_server.domain.member.Role;
//import com.example.opgg_clone_server.domain.member.repository.MemberRepository;
//import com.example.opgg_clone_server.domain.post.Post;
//import com.example.opgg_clone_server.domain.post.repository.PostRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.factory.PasswordEncoderFactories;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.transaction.Transactional;
//
//import static java.lang.Long.parseLong;
//import static java.lang.String.format;
//import static java.lang.String.valueOf;
//
//@RequiredArgsConstructor
//@Component
//public class Seeder {
//
//
//    private final Init init;
//
//
//    @PostConstruct
//    public void init(){
//        init.save();
//    }
//
//    /*참고
//        @PostConstruct는 해당 빈 자체만 생성되었다고 가정하고 호출됩니다.
//        해당 빈에 관련된 AOP등을 포함한, 전체 스프링 애플리케이션 컨텍스트가 초기화 된 것을 의미하지는 않습니다.
//        트랜잭션을 처리하는 AOP등은 스프링의 후 처리기(post processer)가 완전히 동작을 끝내서,
//        스프링 애플리케이션 컨텍스트의 초기화가 완료되어야 적용됩니다.
//        정리하면 @PostConstruct는 해당 빈의 AOP 적용을 보장하지 않습니다.
//    */
//    @RequiredArgsConstructor
//    @Component
//    private static class Init{
//        private final MemberRepository memberRepository;
//
//        private final PostRepository postRepository;
//        private final CommentRepository commentRepository;
//
//        @Transactional
//        public void save() {
//            PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//
//
//            //== MEMBER 저장 ==//
//            memberRepository.save(Member.builder().username("username1").password(delegatingPasswordEncoder.encode("1234567890")).name("USER1").nickName("밥 잘먹는 동훈이1").role(Role.USER).age(22).build());
//
//            memberRepository.save(Member.builder().username("username2").password(delegatingPasswordEncoder.encode("1234567890")).name("USER2").nickName("밥 잘먹는 동훈이2").role(Role.USER).age(22).build());
//
//            memberRepository.save(Member.builder().username("username3").password(delegatingPasswordEncoder.encode("1234567890")).name("USER3").nickName("밥 잘먹는 동훈이3").role(Role.USER).age(22).build());
//
//            Member member = memberRepository.findById(1L).orElse(null);
//
//
//            for(int i = 0; i<=50; i++ ){
//                Post post = Post.builder().title(format("게시글 %s", i)).content(format("내용 %s", i)).build();
//                post.confirmWriter(memberRepository.findById((long) (i % 3 + 1)).orElse(null));
//                postRepository.save(post);
//            }
//
//            for(int i = 1; i<=150; i++ ){
//                Comment comment = Comment.builder().content("댓글" + i).build();
//                comment.confirmWriter(memberRepository.findById((long) (i % 3 + 1)).orElse(null));
//
//                comment.confirmPost(postRepository.findById((long)(i%50 + 1)).orElse(null));
//                commentRepository.save(comment);
//            }
//
//
//            commentRepository.findAll().stream().forEach(comment -> {
//
//                for(int i = 1; i<=50; i++ ){
//                    Comment recomment = Comment.builder().content("대댓글" + i).build();
//                    recomment.confirmWriter(memberRepository.findById((long) (i % 3 + 1)).orElse(null));
//
//                    recomment.confirmPost(comment.getPost());
//                    recomment.confirmParent(comment);
//                    commentRepository.save(recomment);
//                }
//
//            });
//        }
//    }
//
//
//}