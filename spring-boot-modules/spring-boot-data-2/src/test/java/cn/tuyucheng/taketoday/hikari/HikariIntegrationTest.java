package cn.tuyucheng.taketoday.hikari;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class HikariIntegrationTest {

   @Autowired
   private DataSource dataSource;

   @Test
   void hikariConnectionPoolIsConfigured() {
      assertEquals("com.zaxxer.hikari.HikariDataSource", dataSource.getClass()
            .getName());
   }
}