package cn.tuyucheng.taketoday.spring.data.couchbase.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

class PersonTemplateServiceLiveTest extends PersonServiceLiveTest {

   @Autowired
   @Qualifier("PersonTemplateService")
   void setPersonService(PersonService service) {
      this.personService = service;
   }
}
