package cn.tuyucheng.taketoday.manytomany;

import cn.tuyucheng.taketoday.manytomany.model.Course;
import cn.tuyucheng.taketoday.manytomany.model.CourseRating;
import cn.tuyucheng.taketoday.manytomany.model.CourseRatingKey;
import cn.tuyucheng.taketoday.manytomany.model.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ManyToManyTestConfiguration.class)
@DirtiesContext
@Transactional
class ManyToManyIntegrationTest {

   @PersistenceContext
   EntityManager entityManager;

   @Test
   void contextStarted() {
   }

   @Test
   void whenCourseRatingPersisted_thenCorrect() {
      Student student = new Student(101L);
      entityManager.persist(student);

      Course course = new Course(201L);
      entityManager.persist(course);

      CourseRating courseRating = new CourseRating();
      courseRating.setId(new CourseRatingKey());
      courseRating.setStudent(student);
      courseRating.setCourse(course);
      courseRating.setRating(100);
      entityManager.persist(courseRating);

      CourseRating persistedCourseRating = entityManager.find(CourseRating.class, new CourseRatingKey(101L, 201L));

      assertThat(persistedCourseRating, notNullValue());
      assertThat(persistedCourseRating.getStudent().getId(), is(101L));
      assertThat(persistedCourseRating.getCourse().getId(), is(201L));
   }
}