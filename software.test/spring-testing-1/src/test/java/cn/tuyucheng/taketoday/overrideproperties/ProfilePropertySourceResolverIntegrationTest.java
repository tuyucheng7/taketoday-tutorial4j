package cn.tuyucheng.taketoday.overrideproperties;

import cn.tuyucheng.taketoday.overrideproperties.resolver.PropertySourceResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@EnableWebMvc
class ProfilePropertySourceResolverIntegrationTest {

   @Autowired
   private PropertySourceResolver propertySourceResolver;

   @Test
   void shouldProfiledProperty_overridePropertyValues() {
      final String firstProperty = propertySourceResolver.getFirstProperty();
      final String secondProperty = propertySourceResolver.getSecondProperty();

      assertEquals("profile", firstProperty);
      assertEquals("file", secondProperty);
   }
}