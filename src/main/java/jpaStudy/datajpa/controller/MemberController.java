package jpaStudy.datajpa.controller;

import jakarta.annotation.PostConstruct;
import jpaStudy.datajpa.entity.Member;
import jpaStudy.datajpa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member){  //도메인 클래스 컨버터
        return member.getUsername();                                //조회할 때만 사용하기.
    }

    @PostConstruct
    public void init(){
        memberRepository.save(new Member("member1"));
    }
}
