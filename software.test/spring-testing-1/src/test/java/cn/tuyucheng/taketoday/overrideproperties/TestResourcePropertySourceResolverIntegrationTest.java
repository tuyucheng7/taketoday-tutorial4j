package cn.tuyucheng.taketoday.overrideproperties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import cn.tuyucheng.taketoday.overrideproperties.resolver.PropertySourceResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EnableWebMvc
class TestResourcePropertySourceResolverIntegrationTest {

   @Autowired
   private PropertySourceResolver propertySourceResolver;

   @Test
   void shouldTestResourceFile_overridePropertyValues() {
      final String firstProperty = propertySourceResolver.getFirstProperty();
      final String secondProperty = propertySourceResolver.getSecondProperty();

      assertEquals("file", firstProperty);
      assertEquals("file", secondProperty);
   }
}