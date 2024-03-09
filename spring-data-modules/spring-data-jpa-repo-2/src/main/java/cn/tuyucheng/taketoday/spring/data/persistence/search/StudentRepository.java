package cn.tuyucheng.taketoday.spring.data.persistence.search;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

   Student findFirstByOrderByScoreDesc();

   Student findFirstBy(Sort sort);

   Student findFirstByNameLike(String name, Sort sort);

   List<Student> findFirst3ByOrderByScoreDesc();

   List<Student> findFirst2ByScoreBetween(int startScore, int endScore, Sort sort);

   Student findTopByOrderByScoreDesc();

   Student findTopBy(Sort sort);

   Student findTopByNameLike(String name, Sort sort);

   List<Student> findTop3ByOrderByScoreDesc();

   List<Student> findTop2ByScoreBetween(int startScore, int endScore, Sort sort);
}