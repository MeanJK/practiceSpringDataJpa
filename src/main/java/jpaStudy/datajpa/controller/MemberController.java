package jpaStudy.datajpa.controller;

import jakarta.annotation.PostConstruct;
import jpaStudy.datajpa.dto.MemberDto;
import jpaStudy.datajpa.entity.Member;
import jpaStudy.datajpa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/members")
    public Page<MemberDto> listMembers(Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        //PageRequest request = PageRequest.of(1, 2) 이런식으로 구현체를 직접 만들어서 페이지를 지정할 수 있다.
        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        return map;
    }

    @PostConstruct
    public void init(){
        for(int i = 0; i <= 100; i++){
            memberRepository.save(new Member("user"+i, i));
        }
    }
}
