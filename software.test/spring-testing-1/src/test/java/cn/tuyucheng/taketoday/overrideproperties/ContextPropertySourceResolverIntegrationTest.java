package cn.tuyucheng.taketoday.overrideproperties;

import cn.tuyucheng.taketoday.overrideproperties.resolver.PropertySourceResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(initializers = PropertyOverrideContextInitializer.class, classes = TestApplication.class)
class ContextPropertySourceResolverIntegrationTest {

   @Autowired
   private PropertySourceResolver propertySourceResolver;

   @Test
   void shouldContext_overridePropertyValues() {
      final String firstProperty = propertySourceResolver.getFirstProperty();
      final String secondProperty = propertySourceResolver.getSecondProperty();

      assertEquals(PropertyOverrideContextInitializer.PROPERTY_FIRST_VALUE, firstProperty);
      assertEquals("contextFile", secondProperty);
   }
}