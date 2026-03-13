package kr.spartaclub.cloudarchitecture.service;

import kr.spartaclub.cloudarchitecture.dto.MemberCreateRequest;
import kr.spartaclub.cloudarchitecture.dto.MemberResponse;
import kr.spartaclub.cloudarchitecture.dto.ProfileImageUploadResponse;
import kr.spartaclub.cloudarchitecture.dto.ProfileImageUrlResponse;
import kr.spartaclub.cloudarchitecture.entity.Member;
import kr.spartaclub.cloudarchitecture.exception.MemberNotFoundException;
import kr.spartaclub.cloudarchitecture.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final S3Service s3Service;

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

    @Transactional
    public ProfileImageUploadResponse uploadProfileImage(Long memberId, MultipartFile file) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        String key = s3Service.uploadProfileImage(memberId, file);
        member.updateProfileImageKey(key);

        return new ProfileImageUploadResponse(member.getId(), key);
    }

    public ProfileImageUrlResponse getProfileImageUrl(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        if (member.getProfileImageKey() == null || member.getProfileImageKey().isBlank()) {
            throw new IllegalArgumentException("프로필 이미지가 존재하지 않습니다.");
        }

        String url = s3Service.createPresignedGetUrl(member.getProfileImageKey());
        return new ProfileImageUrlResponse(member.getId(), url);
    }
}