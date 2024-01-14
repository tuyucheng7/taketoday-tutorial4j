package cn.tuyucheng.taketoday.trifunction;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VavrFunction3ExampleUnitTest {

   @Test
   void whenMultiplyThenAdd_ThenReturnsCorrectResult() {
      assertEquals(25, VavrFunction3Example.multiplyThenAdd.apply(2, 10, 5));
   }

   @Test
   void whenMultiplyThenAddThenDivideByTen_ThenReturnsCorrectResult() {
      assertEquals(2, VavrFunction3Example.multiplyThenAddThenDivideByTen.apply(2, 10, 5));
   }

}
