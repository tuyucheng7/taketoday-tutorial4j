package cn.tuyucheng.taketoday.activitiwithspring;

import cn.tuyucheng.taketoday.activiti.security.withspring.SpringSecurityActivitiApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringSecurityActivitiApplication.class)
@WebAppConfiguration
@AutoConfigureTestDatabase
class ActivitiSpringSecurityIntegrationTest {

   @Test
   void contextLoads() {
   }
}