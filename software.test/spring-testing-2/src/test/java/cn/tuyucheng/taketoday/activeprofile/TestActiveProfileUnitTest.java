package cn.tuyucheng.taketoday.activeprofile;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ActiveProfileApplication.class)
@ActiveProfiles(value = "test")
class TestActiveProfileUnitTest {

   @Value("${profile.property.value}")
   private String propertyString;

   @Test
   void whenTestIsActive_thenValueShouldBeKeptFromApplicationTestYaml() {
      assertEquals("This the the application-test.yaml file", propertyString);
   }
}