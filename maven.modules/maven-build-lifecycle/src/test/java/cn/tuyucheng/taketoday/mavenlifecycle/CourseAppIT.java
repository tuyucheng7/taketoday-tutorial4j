package cn.tuyucheng.taketoday.mavenlifecycle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CourseAppIT {

   @Test
   void givenIntegrationTest_whenGetCourse_ThenCourseShouldBePresent() {
      CourseApp courseApp = new CourseApp();

      assertEquals("√ Spring Masterclass", courseApp.getCourse());
   }
}