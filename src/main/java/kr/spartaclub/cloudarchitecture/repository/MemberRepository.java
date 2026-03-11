package kr.spartaclub.cloudarchitecture.repository;

import kr.spartaclub.cloudarchitecture.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
