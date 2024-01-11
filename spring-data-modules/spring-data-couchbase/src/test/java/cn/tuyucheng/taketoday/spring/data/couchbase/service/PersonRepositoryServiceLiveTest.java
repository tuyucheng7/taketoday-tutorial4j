package cn.tuyucheng.taketoday.spring.data.couchbase.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

class PersonRepositoryServiceLiveTest extends PersonServiceLiveTest {

   @Autowired
   @Qualifier("PersonRepositoryService")
   void setPersonService(PersonService service) {
      this.personService = service;
   }
}
