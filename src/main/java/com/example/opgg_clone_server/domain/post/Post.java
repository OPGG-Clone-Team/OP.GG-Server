package com.example.opgg_clone_server.domain.post;

import com.example.opgg_clone_server.domain.BaseTimeEntity;
import com.example.opgg_clone_server.domain.comment.Comment;
import com.example.opgg_clone_server.domain.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.IDENTITY;

@Table(name = "POST")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_id")
    private Long id;

    // JpaRepository로 Post entity 조회 시 Member와 양방향 매핑이 되어 있기 때문에 member 테이블도 같이 조인해서 조회함.
    // 이 때 cost가 발생하기 때문에 실제로 Post를 조회할 때는 연관된 member는 proxy 객체로 조회되고,
    // 해당 post 객체의 member를 get method로 가져올 때 실질적으로 쿼리가 이루어진다.
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @Column(length = 40, nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = true)
    private String filePath;


    //== 게시글을 삭제하면 달려있는 댓글 모두 삭제 ==//
    @OneToMany(mappedBy = "post", cascade = ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();


    //== 연관관계 메서드 ==//
    public void confirmWriter(Member writer) {
        //writer는 변경이 불가능하므로 이렇게만 해주어도 될듯
        this.writer = writer;
        writer.addPost(this);
    }
    public void addComment(Comment comment){
        commentList.add(comment);
    }


    //== 내용 수정 ==//
    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateFilePath(String filePath) {
        this.filePath = filePath;
    }
}