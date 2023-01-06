package cn.tuyucheng.taketoday.largeresultset;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
	Slice<Student> findAllByFirstName(String firstName, Pageable page);

	Page<Student> findAllByLastName(String firstName, Pageable page);

	Stream<Student> findAllByFirstName(String firstName);
}