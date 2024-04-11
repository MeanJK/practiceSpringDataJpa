package jpaStudy.datajpa.repository;

import jpaStudy.datajpa.dto.MemberDto;
import jpaStudy.datajpa.entity.Member;
import jpaStudy.datajpa.entity.Team;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member saveMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(saveMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {

        Member member1 = new Member("cham", 15);
        Member member2 = new Member("chambi", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("chambi", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("chambi");
        assertThat(result.get(0).getAge()).isEqualTo(20);
    }

    @Test
    public void testFindUser(){
        Member member1 = new Member("cham", 15);
        Member member2 = new Member("chambi", 15);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findUser("cham", 15);
        Member findMember = members.get(0);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void testFindUsernameList(){
        Member member1 = new Member("cham", 15);
        Member member2 = new Member("chambi", 15);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> usernameList = memberRepository.findUsernameList();
        assertThat(usernameList.get(0)).isEqualTo("cham");
        assertThat(usernameList.get(1)).isEqualTo("chambi");
    }

    @Test
    public void testFindDto() {
        Team team1 = new Team("team1");
        teamRepository.save(team1);

        Member member1 = new Member("cham", 15);
        member1.setTeam(team1);

        memberRepository.save(member1);

        List<MemberDto> memberDtoList = memberRepository.findMemberDtoList();
        for (MemberDto memberDto : memberDtoList) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void testFindByNames(){
        Member member1 = new Member("cham", 15);
        Member member2 = new Member("chambi", 15);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> names = memberRepository.findByNames(Arrays.asList("cham", "chambi"));
        assertThat(names.get(0)).isEqualTo(member1);
        assertThat(names.get(1)).isEqualTo(member2);
    }
}