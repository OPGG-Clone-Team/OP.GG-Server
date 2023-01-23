package com.example.opgg_clone_server.domain.member.dto;

import com.example.opgg_clone_server.domain.member.Member;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


// Record Class : 불변 데이터 객체 (final 멤버변수)를 헤더(class 정의부, 아래에서 MemberSignUpDto의 매개변수들)를 통해 자동으로 생성
// 자동 생성되는 getter는 getX()가 아니라, X()로 생성
public record MemberSignUpDto(@NotBlank(message = "아이디를 입력해주세요")
                              @Size(min = 7, max = 25, message = "아이디를 7~25자 내외로 입력해주세요")
                              String username,

                              @NotBlank(message = "비밀번호를 입력해주세요")
                              @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
                                      message = "비밀번호는 8~30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야 합니다.")
                              String password,

                              @NotBlank(message = "이름을 입력해주세요") @Size(min=2, message = "사용자 이름을 두 자 이상 입력해주세요.")
                              @Pattern(regexp = "^[A-Za-z가-힣]+$", message = "사용자 이름은 한글 또는 알파벳만 입력해주세요.")
                              String name,

                              @NotBlank(message = "닉네임을 입력해주세요.")
                              @Size(min=2, message = "닉네임을 두 자 이상 입력해주세요.")
                              @NotBlank String nickName,

                              @NotNull(message = "나이를 입력해주세요")
                              @Range(min = 0, max = 150, message = "유효한 나이를 입력해주세요.")
                              Integer age) {

    public Member toEntity() {
        return Member.builder().username(username).password(password).name(name).nickName(nickName).age(age).build();
    }
}