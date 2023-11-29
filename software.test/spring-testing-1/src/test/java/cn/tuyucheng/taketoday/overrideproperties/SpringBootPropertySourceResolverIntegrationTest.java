package cn.tuyucheng.taketoday.overrideproperties;

import cn.tuyucheng.taketoday.overrideproperties.resolver.PropertySourceResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {"example.firstProperty=annotation"})
@EnableWebMvc
class SpringBootPropertySourceResolverIntegrationTest {

   @Autowired
   private PropertySourceResolver propertySourceResolver;

   @Test
   void shouldSpringBootTestAnnotation_overridePropertyValues() {
      final String firstProperty = propertySourceResolver.getFirstProperty();
      final String secondProperty = propertySourceResolver.getSecondProperty();

      assertEquals("annotation", firstProperty);
      assertEquals("file", secondProperty);
   }
}