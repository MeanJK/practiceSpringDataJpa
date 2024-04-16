package jpaStudy.datajpa.repository;

import jakarta.persistence.EntityManager;
import jpaStudy.datajpa.entity.Member;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final EntityManager em;

    // QueryDsl 사용할 때 Custom을 많이 사용한다.
    @Override
    public List<Member> findMemberCustom(){
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
