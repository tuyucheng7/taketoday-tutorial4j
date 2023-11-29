package cn.tuyucheng.taketoday.activeprofile;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ActiveProfileApplication.class)
@EnabledIf(value = "#{{'test', 'prod'}.contains(environment.getActiveProfiles()[0])}", loadContext = true)
@ActiveProfiles(value = "test")
class MultipleActiveProfileUnitTest {

   @Value("${profile.property.value}")
   private String propertyString;

   @Autowired
   private Environment env;

   @Test
   void whenDevIsActive_thenValueShouldBeKeptFromDedicatedApplicationYaml() {
      String currentProfile = env.getActiveProfiles()[0];
      assertEquals(String.format("This the the application-%s.yaml file", currentProfile), propertyString);
   }
}