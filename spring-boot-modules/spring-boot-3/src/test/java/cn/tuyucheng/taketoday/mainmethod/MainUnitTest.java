package cn.tuyucheng.taketoday.mainmethod;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.UseMainMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(useMainMethod = UseMainMethod.ALWAYS)
public class MainUnitTest {

   @Value("${customProperty}")
   private String customProperty;

   @Test
   void whenUseMainMethodSetToAlways_thenShouldLoadCustomProperty() {
      assertEquals("customValue", customProperty);
   }
}