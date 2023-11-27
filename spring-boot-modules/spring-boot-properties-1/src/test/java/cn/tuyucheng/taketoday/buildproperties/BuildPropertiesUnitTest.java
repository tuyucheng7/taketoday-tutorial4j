package cn.tuyucheng.taketoday.buildproperties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class BuildPropertiesUnitTest {
   @Autowired
   private BuildProperties buildProperties;

   @Test
   void givenBuildPropertiesBean_WhenFetchDefaultBuildProperties_ThenGetValidValues() {
      assertEquals("spring-boot-properties-1", buildProperties.getArtifact());
      assertEquals("cn.tuyucheng.taketoday", buildProperties.getGroup());
      assertEquals("1.0.0", buildProperties.getVersion());
   }

   @Test
   void givenBuildPropertiesBean_WhenFetchCustomBuildProperties_ThenGetValidValues() {
      assertEquals("123", buildProperties.get("custom.value"));
      assertNotNull(buildProperties.get("java.version"));
      assertEquals("Spring Boot Properties Module", buildProperties.get("description"));
   }
}