package cn.tuyucheng.taketoday.activeprofile;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ActiveProfileApplication.class)
class DevActiveProfileUnitTest {

   @Value("${profile.property.value}")
   private String propertyString;

   @Test
   void whenDevIsActive_thenValueShouldBeKeptFromApplicationYaml() {
      assertEquals("This the the application.yaml file", propertyString);
   }
}