package jpaStudy.datajpa.repository;

import jpaStudy.datajpa.entity.Member;
import jpaStudy.datajpa.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;
    @Autowired MemberRepository memberRepository;

    @Test
    public void memberTest(){
        Member member = new Member("memberA");
        Member saveMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(saveMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    public void basicCRUD(){

        Member member1 = new Member("memberA");
        Member member2 = new Member("memberB");

        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //전체 조회 검증
        List<Member> members = memberRepository.findAll();
        assertThat(members.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan(){

        Member member1 = new Member("cham", 15);
        Member member2 = new Member("chambi", 20);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("chambi", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("chambi");
        assertThat(result.get(0).getAge()).isEqualTo(20);
    }

    @Test
    public void findByNamedQuery(){
        Member member1 = new Member("cham", 15);

        memberJpaRepository.save(member1);
        List<Member> findMember = memberJpaRepository.findByUserName("cham");

        assertThat(findMember.get(0)).isEqualTo(member1);
    }

    @Test
    public void testPaging(){
        //given
        memberJpaRepository.save(new Member("aaa", 10));
        memberJpaRepository.save(new Member("bbb", 10));
        memberJpaRepository.save(new Member("ccc", 10));
        memberJpaRepository.save(new Member("ddd", 10));
        memberJpaRepository.save(new Member("eee", 10));
        //when
        int age = 10;
        int offset = 0;
        int limit = 3;
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        //페이지 계산 공식
        //totalPage = totalCount / size ...
        //마지막 페이지 ...
        //최종 페이지 ...

        //then
        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
    }

    @Test
    public void bulkUpdate(){

        //given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 15));
        memberJpaRepository.save(new Member("member3", 20));
        memberJpaRepository.save(new Member("member4", 22));
        memberJpaRepository.save(new Member("member5", 25));

        //when
        int resultCount = memberJpaRepository.bulkAgePlus(20);

        //then
        assertThat(resultCount).isEqualTo(3);
    }
}