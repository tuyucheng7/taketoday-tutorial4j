package cn.tuyucheng.taketoday.mybatis.spring;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "classpath:/beans.xml")
class ArticleMapperXMLUnitTest extends ArticleMapperCommonUnitTest {
}