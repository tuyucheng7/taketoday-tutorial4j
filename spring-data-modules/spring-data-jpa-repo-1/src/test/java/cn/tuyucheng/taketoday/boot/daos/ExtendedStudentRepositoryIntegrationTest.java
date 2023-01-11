package cn.tuyucheng.taketoday.boot.daos;

import cn.tuyucheng.taketoday.SpringDataRepositoryApplication;
import cn.tuyucheng.taketoday.boot.domain.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringDataRepositoryApplication.class})
@DirtiesContext
class ExtendedStudentRepositoryIntegrationTest {
	@Resource
	private ExtendedStudentRepository extendedStudentRepository;

	@BeforeEach
	void setup() {
		Student student = new Student(1, "john");
		extendedStudentRepository.save(student);
		Student student2 = new Student(2, "johnson");
		extendedStudentRepository.save(student2);
		Student student3 = new Student(3, "tom");
		extendedStudentRepository.save(student3);
	}

	@Test
	void givenStudents_whenFindByName_thenGetOk() {
		List<Student> students = extendedStudentRepository.findByAttributeContainsText("name", "john");
		assertThat(students.size()).isEqualTo(2);
	}
}