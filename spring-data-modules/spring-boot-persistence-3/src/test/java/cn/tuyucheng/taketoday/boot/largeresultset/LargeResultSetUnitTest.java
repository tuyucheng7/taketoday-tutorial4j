package cn.tuyucheng.taketoday.boot.largeresultset;

import cn.tuyucheng.taketoday.largeresultset.LargeResultSetApplication;
import cn.tuyucheng.taketoday.largeresultset.Student;
import cn.tuyucheng.taketoday.largeresultset.StudentRepository;
import cn.tuyucheng.taketoday.largeresultset.service.EmailService;
import cn.tuyucheng.taketoday.largeresultset.service.StudentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = LargeResultSetApplication.class)
class LargeResultSetUnitTest {

	@Autowired
	private StudentRepository repository;

	@Autowired
	private StudentService studentService;

	@MockBean
	private EmailService mockEmailService;

	@AfterEach
	public void afterEach() {
		repository.deleteAll();
	}

	@Test
	void givenTwelveRowsMatchingCriteria_whenRetrievingDataSliceBySlice_allDataIsProcessed() {
		saveStudents(12);

		studentService.processStudentsByFirstName("john");

		verify(mockEmailService, times(12)).sendEmailToStudent(any());
	}

	@Test
	void givenTwelveRowsMatchingCriteria_whenRetrievingDataPageByPage_allDataIsProcessed() {
		saveStudents(12);

		studentService.processStudentsByLastName("doe");

		verify(mockEmailService, times(12)).sendEmailToStudent(any());
	}

	@Test
	void processStudentsByFirstNameUsingStreams() {
		saveStudents(12);

		studentService.processStudentsByFirstNameUsingStreams("john");

		verify(mockEmailService, times(12)).sendEmailToStudent(any());
	}

	private void saveStudents(int count) {
		List<Student> students = IntStream.range(0, count)
				.boxed()
				.map(i -> new Student("john", "doe"))
				.collect(Collectors.toList());
		repository.saveAll(students);
	}
}