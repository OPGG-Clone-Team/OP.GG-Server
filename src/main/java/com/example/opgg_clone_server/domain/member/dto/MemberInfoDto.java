package com.example.opgg_clone_server.domain.member.dto;

import com.example.opgg_clone_server.domain.member.Member;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberInfoDto {

    private String name;
    private String nickName;
    private String username;
    private Integer age;


    // builder annotation 사용 시 class level에 붙이게 되면, final 멤버 변수들이 builder pattern에 포함되지 않음
    @Builder
    public MemberInfoDto(Member member) {
        this.name = member.getName();
        this.nickName = member.getNickName();
        this.username = member.getUsername();
        this.age = member.getAge();
    }
}