package jpaStudy.datajpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpaStudy.datajpa.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void memberTest() {

        Team team1 = new Team("TeamA");
        Team team2 = new Team("TeamB");

        em.persist(team1);
        em.persist(team2);

        Member member1 = new Member("memberA", 10, team1);
        Member member2 = new Member("memberB", 20, team1);
        Member member3 = new Member("memberC", 30, team2);
        Member member4 = new Member("memberD", 40, team2);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        //초기화
        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.team = " + member.getTeam());
        }
    }
    
    @Test
    void time_테스트() throws Exception {
        //given
        Member member1 = new Member("member1");
        memberRepository.save(member1);

        Thread.sleep(1000);
        member1.setUsername("member2");

        em.flush();
        em.clear();
        //when
        Member member = memberRepository.findById(member1.getId()).get();
        //then
        System.out.println("member.getUpdatedDate() = " + member.getUpdatedDate());
        System.out.println("member.getCreatedDate() = " + member.getCreatedDate());
    }
}