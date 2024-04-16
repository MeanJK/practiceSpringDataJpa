package jpaStudy.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    EntityManager em;

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

//        memberRepository.findTop3ByAge(age) 맨 앞에 3건만 가져오고 싶다면 페이징 넘기지 말고 다음과 같이 하자!
        Page<Member> pages = memberRepository.findByAge(age, pageRequest);

        //Member 엔티티를 외부에 반환하면 좋지 않다고 전부터 누누히 말했다. Dto로 변환시켜 반환하자.
        //엔티티를 외부에 노출시키면 API 스펙이 바뀌어버리기 때문에 에러가 발생할 가능성이 상당히 높다.
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

    @Test
    public void bulkUpdate(){

        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 15));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 22));
        memberRepository.save(new Member("member5", 25));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void fetchJoinTest(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member memberA = new Member("memberA", 10, teamA);
        Member memberB = new Member("memberB", 10, teamB);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        em.flush();
        em.clear();
        //Entity Graph는 fetchJoin이라 할 수 있다!
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
        List<Member> members2 = memberRepository.findMemberFetchJoin();
        for (Member member : members2) {
            System.out.println("member = " + member);
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }
    
    @Test
    void 쿼리_힌트_테스트() throws Exception {
        //given
        Member member = new Member("min", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();
        //when
        Member member1 = memberRepository.findOnlyByUsername("min");
        member1.setUsername("min2");
        em.flush();
        //then
    }
}