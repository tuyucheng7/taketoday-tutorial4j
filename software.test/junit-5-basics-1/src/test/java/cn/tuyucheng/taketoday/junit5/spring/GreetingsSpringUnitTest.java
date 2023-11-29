package cn.tuyucheng.taketoday.junit5.spring;

import cn.tuyucheng.taketoday.junit5.Greetings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringTestConfiguration.class})
public class GreetingsSpringUnitTest {

   @Test
   void whenCallingSayHello_thenReturnHello() {
      assertEquals("Hello", Greetings.sayHello());
   }
}