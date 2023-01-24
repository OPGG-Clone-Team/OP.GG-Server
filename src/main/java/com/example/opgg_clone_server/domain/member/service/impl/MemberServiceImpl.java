package com.example.opgg_clone_server.domain.member.service.impl;

import com.example.opgg_clone_server.domain.member.Member;
import com.example.opgg_clone_server.domain.member.dto.MemberInfoDto;
import com.example.opgg_clone_server.domain.member.dto.MemberSignUpDto;
import com.example.opgg_clone_server.domain.member.dto.MemberUpdateDto;
import com.example.opgg_clone_server.domain.member.exception.MemberException;
import com.example.opgg_clone_server.domain.member.exception.MemberExceptionType;
import com.example.opgg_clone_server.domain.member.repository.MemberRepository;
import com.example.opgg_clone_server.domain.member.service.MemberService;
import com.example.opgg_clone_server.global.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void signUp(MemberSignUpDto memberSignUpDto) throws Exception {

        // 변경 : entity 전환하기 전에 우선 이미 존재하는 아이디가 있는지부터 validate
        if(memberRepository.findByUsername(memberSignUpDto.username()).isPresent()){
            throw new MemberException(MemberExceptionType.ALREADY_EXIST_USERNAME);
        }

        Member member = memberSignUpDto.toEntity();
        member.addUserAuthority();
        member.encodePassword(passwordEncoder);
        memberRepository.save(member);
    }

    @Override
    public void update(MemberUpdateDto memberUpdateDto) throws Exception {

        // member 자신의 상태를 변경하는 경우밖에 없기 떄문에
        // username을 파라미터로 넣지 않고 자신의 인증정보를 이용하여 username을 가져오는 전략
        Member member = memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)
        );

        memberUpdateDto.age().ifPresent(member::updateAge);
        memberUpdateDto.name().ifPresent(member::updateName);
        memberUpdateDto.nickName().ifPresent(member::updateNickName);
    }


    @Override
    public void updatePassword(String originPassword, String changedPassword) throws Exception {
        Member member = memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));


        if(!member.matchPassword(passwordEncoder, originPassword) ) {
            throw new MemberException(MemberExceptionType.WRONG_PASSWORD);
        }

        member.updatePassword(passwordEncoder, changedPassword);
    }


    @Override
    public void withdraw(String checkPassword) throws Exception {
        Member member = memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new Exception("회원이 존재하지 않습니다"));

        if(!member.matchPassword(passwordEncoder, checkPassword) ) {
            throw new MemberException(MemberExceptionType.WRONG_PASSWORD);
        }

        memberRepository.delete(member);
    }

    @Override
    public MemberInfoDto getInfo(Long id) throws Exception {
        Member findMember = memberRepository.findById(id).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        return new MemberInfoDto(findMember);
    }

    @Override
    public MemberInfoDto getMyInfo() throws Exception {
        Member findMember = memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        return new MemberInfoDto(findMember);
    }
}