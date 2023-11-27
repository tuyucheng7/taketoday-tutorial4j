package cn.tuyucheng.taketoday.profiles;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("dev")
@ContextConfiguration(classes = {SpringProfilesConfig.class}, loader = AnnotationConfigContextLoader.class)
class DevProfileWithAnnotationIntegrationTest {
   @Autowired
   DatasourceConfig datasourceConfig;

   @Test
   void testSpringProfiles() {
      assertTrue(datasourceConfig instanceof DevDatasourceConfig);
   }
}