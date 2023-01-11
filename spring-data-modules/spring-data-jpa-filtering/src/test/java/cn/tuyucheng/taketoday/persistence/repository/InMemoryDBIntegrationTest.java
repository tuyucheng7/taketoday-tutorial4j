package cn.tuyucheng.taketoday.persistence.repository;

import cn.tuyucheng.taketoday.config.StudentJpaConfig;
import cn.tuyucheng.taketoday.inmemory.persistence.dao.StudentRepository;
import cn.tuyucheng.taketoday.inmemory.persistence.model.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {StudentJpaConfig.class}, loader = AnnotationConfigContextLoader.class)
@Transactional
@DirtiesContext
class InMemoryDBIntegrationTest {

	@Resource
	private StudentRepository studentRepository;

	private static final long ID = 1;
	private static final String NAME = "john";

	@Test
	void givenStudent_whenSave_thenGetOk() {
		Student student = new Student(ID, NAME);
		studentRepository.save(student);

		Student student2 = studentRepository.findById(ID).get();
		assertEquals(NAME, student2.getName(), "name incorrect");
	}

	@Test
	void givenStudentWithTags_whenSave_thenGetByTagOk() {
		Student student = new Student(ID, NAME);
		student.setTags(Arrays.asList("full time", "computer science"));
		studentRepository.save(student);

		Student student2 = studentRepository.retrieveByTag("full time").get(0);
		assertEquals(NAME, student2.getName(), "name incorrect");
	}

	@Test
	void givenMultipleStudentsWithTags_whenSave_thenGetByTagReturnsCorrectCount() {
		Student student1 = new Student(0, "Larry");
		student1.setTags(Arrays.asList("full time", "computer science"));
		studentRepository.save(student1);

		Student student2 = new Student(1, "Curly");
		student2.setTags(Arrays.asList("part time", "rocket science"));
		studentRepository.save(student2);

		Student student3 = new Student(2, "Moe");
		student3.setTags(Arrays.asList("full time", "philosophy"));
		studentRepository.save(student3);

		Student student4 = new Student(3, "Shemp");
		student4.setTags(Arrays.asList("part time", "mathematics"));
		studentRepository.save(student4);

		List<Student> students = studentRepository.retrieveByTag("full time");
		assertEquals(2, students.size(), "size incorrect");
	}

	@Test
	void givenStudentWithTags_whenSave_thenGetByNameAndTagOk() {
		Student student = new Student(ID, NAME);
		student.setTags(Arrays.asList("full time", "computer science"));
		studentRepository.save(student);

		Student student2 = studentRepository.retrieveByNameFilterByTag("John", "full time").get(0);
		assertEquals(NAME, student2.getName(), "name incorrect");
	}
}