package kr.spartaclub.cloudarchitecture.controller;

import kr.spartaclub.cloudarchitecture.dto.MemberCreateRequest;
import kr.spartaclub.cloudarchitecture.dto.MemberResponse;
import kr.spartaclub.cloudarchitecture.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse createMember(@Valid @RequestBody MemberCreateRequest request) {
        return memberService.createMember(request);
    }

    @GetMapping("/{id}")
    public MemberResponse getMember(@PathVariable Long id) {
        return memberService.getMember(id);
    }
}