package cn.tuyucheng.taketoday.spring.oracle.pooling;

import com.mchange.v2.c3p0.ComboPooledDataSource;
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
@ActiveProfiles({"oracle-pooling-basic", "c3p0"})
class SpringOraclePoolingApplicationC3P0LiveTest {

   @Autowired
   private DataSource dataSource;

   @Test
   void givenC3p0Configuration_thenBuildsComboPooledDataSource() {
      assertInstanceOf(ComboPooledDataSource.class, dataSource);
   }
}