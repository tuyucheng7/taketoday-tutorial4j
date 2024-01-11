package cn.tuyucheng.taketoday.spring.oracle.pooling;

import oracle.jdbc.pool.OracleDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SpringOraclePoolingApplication.class})
@ActiveProfiles({"oracle-pooling-basic", "oracle"})
class SpringOraclePoolingApplicationOracleLiveTest {

   @Autowired
   private DataSource dataSource;

   @Test
   void givenOracleConfiguration_thenBuildsOracleDataSource() {
      assertInstanceOf(OracleDataSource.class, dataSource);
   }
}