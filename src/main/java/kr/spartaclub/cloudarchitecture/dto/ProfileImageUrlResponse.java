package kr.spartaclub.cloudarchitecture.dto;

import lombok.Getter;

@Getter
public class ProfileImageUrlResponse {

    private final Long memberId;
    private final String url;

    public ProfileImageUrlResponse(Long memberId, String url) {
        this.memberId = memberId;
        this.url = url;
    }
}