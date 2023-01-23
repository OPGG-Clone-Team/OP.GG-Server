package com.example.opgg_clone_server.domain.member.dto;

import javax.validation.constraints.NotBlank;

public record MemberWithdrawDto(@NotBlank(message = "비밀번호를 입력해주세요")
                                String checkPassword) {
}