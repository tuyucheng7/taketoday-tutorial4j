package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.junit5.Greetings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GreetingsUnitTest {

   @Test
   void whenCallingSayHello_thenReturnHello() {
      assertEquals("Hello", Greetings.sayHello());
   }
}