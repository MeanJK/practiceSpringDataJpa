package jpaStudy.datajpa.repository;

import jpaStudy.datajpa.dto.MemberDto;
import jpaStudy.datajpa.entity.Member;
import jpaStudy.datajpa.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    public void testResultType(){
        Member member1 = new Member("cham", 15);
        Member member2 = new Member("chambi", 15);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> findMembers = memberRepository.findListByUsername("casasdfsdf");
        System.out.println("findMembers = " + findMembers); //컬렉션은 빈 값이라도 0을 반환

        Member findMember1 = memberRepository.findMemberByUsername("asdfasfasd");
        System.out.println("findMember1 = " + findMember1);  //null 반환

        Optional<Member> findMember2 = memberRepository.findOptionalMemberByUsername("asdfasdfasdf");
        System.out.println("findMember2 = " + findMember2); //Optional.empty반환
    }

    @Test
    public void testPaging(){
        //given
        memberRepository.save(new Member("aaa", 10));
        memberRepository.save(new Member("bbb", 10));
        memberRepository.save(new Member("ccc", 10));
        memberRepository.save(new Member("ddd", 10));
        memberRepository.save(new Member("eee", 10));
        //when
        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));


        Page<Member> pages = memberRepository.findByAge(age, pageRequest);

        //Member 엔티티 상태로 반환하면 좋지 않다고 전부터 누누히 말했다. Dto로 변환시켜 반환하기!
        Page<MemberDto> toMap = pages.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        List<Member> content = pages.getContent();
        long totalCount = pages.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(pages.getTotalElements()).isEqualTo(5);
        assertThat(pages.getNumber()).isEqualTo(0);
        assertThat(pages.getTotalPages()).isEqualTo(2);
        assertThat(pages.isFirst()).isTrue();
        assertThat(pages.hasNext()).isTrue();
    }
}