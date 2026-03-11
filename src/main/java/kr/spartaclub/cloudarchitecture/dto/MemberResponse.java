package kr.spartaclub.cloudarchitecture.dto;

import kr.spartaclub.cloudarchitecture.entity.Member;
import lombok.Getter;

@Getter
public class MemberResponse {

    private final Long id;
    private final String name;
    private final Integer age;
    private final String mbti;
    private final String profileImageKey;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.age = member.getAge();
        this.mbti = member.getMbti();
        this.profileImageKey = member.getProfileImageKey();
    }
}