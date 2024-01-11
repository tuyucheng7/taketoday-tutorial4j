package cn.tuyucheng.taketoday.mybatis.spring;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = PersistenceAutoConfig.class)
class ArticleMapperBootUnitTest extends ArticleMapperCommonUnitTest {
}