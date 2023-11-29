package cn.tuyucheng.taketoday.spring.cloud.aws.rds;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * To run this Live Test, we need to have an AWS account and have API keys generated for programmatic access.
 * <p>
 * Check the README file in this module for more information.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
class SpringCloudRDSLiveTest {

   @Autowired
   DataSource dataSource;

   @Test
   void whenDataSourceCreated_thenSuccess() {
      assertThat(dataSource).isNotNull();
   }

   @Test
   void givenDataSource_whenConnectionCreated_thenSuccess() throws SQLException {
      Connection connection = dataSource.getConnection();
      assertThat(connection).isNotNull();
   }

   @Test
   void givenConnection_whenQueryExecuted_thenSuccess() throws SQLException {
      Connection connection = dataSource.getConnection();
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("SELECT 1");
      while (resultSet.next()) {
         int result = resultSet.getInt(1);
         assertThat(result).isEqualTo(1);
      }
      connection.close();
   }
}