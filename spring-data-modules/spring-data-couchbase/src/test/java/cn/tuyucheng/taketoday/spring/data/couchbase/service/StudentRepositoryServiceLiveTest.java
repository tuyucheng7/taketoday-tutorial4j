package cn.tuyucheng.taketoday.spring.data.couchbase.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

class StudentRepositoryServiceLiveTest extends StudentServiceLiveTest {

   @Autowired
   @Qualifier("StudentRepositoryService")
   void setStudentService(StudentService service) {
      this.studentService = service;
   }
}
