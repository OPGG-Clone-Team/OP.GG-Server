package com.example.opgg_clone_server.domain.member.dto;

import java.util.Optional;


// ? : 이대로만 해두면 DB에 constraints를 걸어두지 않는 이상 시스템에 버그가 생길듯
public record MemberUpdateDto(Optional<String> name, Optional<String> nickName, Optional<Integer> age) {
}