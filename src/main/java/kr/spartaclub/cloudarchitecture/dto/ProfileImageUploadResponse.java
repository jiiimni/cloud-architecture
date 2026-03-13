package kr.spartaclub.cloudarchitecture.dto;

import lombok.Getter;

@Getter
public class ProfileImageUploadResponse {

    private final Long memberId;
    private final String profileImageKey;

    public ProfileImageUploadResponse(Long memberId, String profileImageKey) {
        this.memberId = memberId;
        this.profileImageKey = profileImageKey;
    }
}
