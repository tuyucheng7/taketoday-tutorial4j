package cn.tuyucheng.taketoday.profiles;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("production")
@ContextConfiguration(classes = {SpringProfilesConfig.class}, loader = AnnotationConfigContextLoader.class)
class ProductionProfileWithAnnotationIntegrationTest {

   @Autowired
   DatasourceConfig datasourceConfig;

   @Autowired
   Environment environment;

   @Test
   void testSpringProfiles() {
      for (final String profileName : environment.getActiveProfiles()) {
         System.out.println(STR."Currently active profile - \{profileName}");
      }
      assertEquals("production", environment.getActiveProfiles()[0]);
      assertInstanceOf(ProductionDatasourceConfig.class, datasourceConfig);
   }
}