package com.example.opgg_clone_server.domain.member;

import com.example.opgg_clone_server.domain.BaseTimeEntity;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Table(name = "MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id; // primary Key

    @Column(nullable = false, length = 30, unique = true)
    private String username; // 아이디

    // Column 어노테이션을 넣지 않아도 기본적으로 Entity 안의 멤버변수들은 DB 컬럼으로 등록됨
    // 속성 지정만 하지 않을 뿐임
    private String password; // 비밀번호

    @Column(nullable = false, length = 30)
    private String name; // 이름(실명)

    @Column(nullable = false, length = 30)
    private String nickName; // 별명

    @Column(nullable = false, length = 30)
    private Integer age; // 나이

    @Enumerated(EnumType.STRING)
    private Role role; // 권한 -> USER, ADMIN

    @Column(length = 1000)
    private String refreshToken; // RefreshToken


    // 회원탈퇴 -> 작성한 게시물, 댓글 모두 삭제

    @OneToMany(mappedBy = "writer", cascade = ALL, orphanRemoval = true)
    private List<Post> postList = new ArrayList<>();

    @OneToMany(mappedBy = "writer", cascade = ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();









    //== 정보 수정 ==//
    public void updatePassword(PasswordEncoder passwordEncoder, String password){
        this.password = passwordEncoder.encode(password);
    }

    public void updateName(String name){
        this.name = name;
    }

    public void updateNickName(String nickName){
        this.nickName = nickName;
    }

    public void updateAge(int age){
        this.age = age;
    }

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
    public void destroyRefreshToken(){
        this.refreshToken = null;
    }

    //== 패스워드 암호화 ==//
    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }

}