package jpaStudy.datajpa.repository;

import jpaStudy.datajpa.dto.MemberDto;
import jpaStudy.datajpa.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")  //Query로 값 조회
    List<String> findUsernameList();

    @Query("select new jpaStudy.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDtoList();  //@Query, DTO로 값 조회

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names); // 파라미터 바인딩

    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalMemberByUsername(String username); //단건 Optional

    //@Query(value = "select m from Member m left join m.team t",
        //            countQuery = "select count(m) from Member m");
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)  //업데이는 해당 어노테이션이 들어가야한다, clearAutomatically = true를 해주면 em.clear를 자동으로 해준다.
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);
}