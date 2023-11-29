package cn.tuyucheng.taketoday.restassured.learner;

class CourseNotFoundException extends RuntimeException {

   CourseNotFoundException(String code) {
      super(code);
   }
}
