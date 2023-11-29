package cn.tuyucheng.taketoday.activeprofile;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ActiveProfileApplication.class)
@EnabledIf(value = "#{environment.getActiveProfiles()[0] == 'prod'}", loadContext = true)
@ActiveProfiles(value = "prod")
class ProdActiveProfileUnitTest {

   @Value("${profile.property.value}")
   private String propertyString;

   @Test
   void whenProdIsActive_thenValueShouldBeKeptFromApplicationProdYaml() {
      assertEquals("This the the application-prod.yaml file", propertyString);
   }
}