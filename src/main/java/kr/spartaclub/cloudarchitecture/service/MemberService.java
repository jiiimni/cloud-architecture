package kr.spartaclub.cloudarchitecture.service;

import kr.spartaclub.cloudarchitecture.dto.MemberCreateRequest;
import kr.spartaclub.cloudarchitecture.dto.MemberResponse;
import kr.spartaclub.cloudarchitecture.entity.Member;
import kr.spartaclub.cloudarchitecture.exception.MemberNotFoundException;
import kr.spartaclub.cloudarchitecture.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse createMember(MemberCreateRequest request) {
        Member member = new Member(
                request.getName(),
                request.getAge(),
                request.getMbti()
        );

        Member savedMember = memberRepository.save(member);
        return new MemberResponse(savedMember);
    }

    public MemberResponse getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));

        return new MemberResponse(member);
    }
}