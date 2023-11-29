package cn.tuyucheng.taketoday.junit5.testinstance;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class OrderUnitTest {

   private int sum;

   @BeforeAll
   void init() {
      sum = 1;
   }

   @Test
   @Order(1)
   void firstTest() {
      sum += 2;
      assertEquals(3, sum);
   }

   @Test
   @Order(2)
   void secondTest() {
      sum += 3;
      assertEquals(6, sum);
   }
}