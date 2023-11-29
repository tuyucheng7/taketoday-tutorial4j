package cn.tuyucheng.taketoday.instancio.mockito;

import cn.tuyucheng.taketoday.instancio.student.model.Course;
import cn.tuyucheng.taketoday.instancio.student.model.Grade;
import cn.tuyucheng.taketoday.instancio.student.model.Student;
import cn.tuyucheng.taketoday.instancio.student.service.CourseService;
import cn.tuyucheng.taketoday.instancio.student.service.EnrollmentService;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class InstancioWithMockitoUnitTest {

   @Mock
   private CourseService courseService;

   @InjectMocks
   private EnrollmentService enrollmentService;

   @Test
   void givenStudentWithoutGradeF_thenShouldEnrollStudentInCourse() {
      // Given
      Student student = Instancio.of(Student.class)
            .generate(all(Grade.class), gen -> gen.enumOf(Grade.class).excluding(Grade.F))
            .create();

      Course course = Instancio.create(Course.class);
      when(courseService.getByCode(course.getCode())).thenReturn(course);

      // When
      boolean isEnrolled = enrollmentService.enrollStudent(student, course.getCode());

      // Then
      assertThat(isEnrolled).isTrue();
   }
}