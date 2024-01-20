package cn.tuyucheng.taketoday.mavenlifecycle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CourseAppUnitTest {

   @Test
   void whenGetCourse_ThenCourseShouldBePresent() {
      CourseApp courseApp = new CourseApp();

      assertEquals("Tuyucheng Spring Masterclass", courseApp.getCourse());
   }
}