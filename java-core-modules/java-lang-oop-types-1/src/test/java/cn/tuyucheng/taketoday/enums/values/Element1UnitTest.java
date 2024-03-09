package cn.tuyucheng.taketoday.enums.values;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author chris
 */
public class Element1UnitTest {

   public Element1UnitTest() {
   }

   @BeforeAll
   public static void setUpClass() {
   }

   @AfterAll
   public static void tearDownClass() {
   }

   @BeforeEach
   public void setUp() {
   }

   @AfterEach
   public void tearDown() {
   }

   @Test
   public void whenAccessingToString_thenItShouldEqualName() {
      for (Element1 e1 : Element1.values()) {
         assertEquals(e1.name(), e1.toString());
      }
   }

   @Test
   public void whenCallingValueOf_thenReturnTheCorrectEnum() {
      for (Element1 e1 : Element1.values()) {
         assertSame(e1, Element1.valueOf(e1.name()));
      }
   }
}
