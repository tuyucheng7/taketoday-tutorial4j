package cn.tuyucheng.taketoday.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LoggingFilterAutowiringSupportUnitTest {

   @Autowired
   private LoggingFilterAutowiringSupport loggingFilter;

   @Test
   void givenFilter_whenAutowired_thenDependencyInjected() throws Exception {
      assertNotNull(loggingFilter);
      assertNotNull(getField(loggingFilter, "loggingService"));
   }

   private Object getField(Object target, String fieldName) throws NoSuchFieldException, IllegalAccessException {
      Field field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(target);
   }
}