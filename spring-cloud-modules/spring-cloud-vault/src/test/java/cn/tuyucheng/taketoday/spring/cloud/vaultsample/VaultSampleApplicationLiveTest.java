package cn.tuyucheng.taketoday.spring.cloud.vaultsample;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class VaultSampleApplicationLiveTest {

   @Autowired
   Environment env;

   @Autowired
   DataSource datasource;

   @Test
   void whenGenericBackendEnabled_thenEnvHasAccessToVaultSecrets() {
      String fooValue = env.getProperty("foo");
      assertEquals("test-bar", fooValue);
   }

   @Test
   void whenKvBackendEnabled_thenEnvHasAccessToVaultSecrets() {
      String fooValue = env.getProperty("foo.versioned");
      assertEquals("bar1", fooValue);
   }

   @Test
   void whenDatabaseBackendEnabled_thenDatasourceUsesVaultCredentials() {
      try (Connection c = datasource.getConnection()) {
         ResultSet rs = c.createStatement().executeQuery("select 1");

         rs.next();
         Long value = rs.getLong(1);

         assertEquals(Long.valueOf(1), value);
      } catch (SQLException sex) {
         throw new RuntimeException(sex);
      }
   }
}