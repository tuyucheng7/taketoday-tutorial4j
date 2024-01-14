package cn.tuyucheng.taketoday.diamondoperator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DiamondOperatorUnitTest {
   @Test
   public void whenCreateCarUsingDiamondOperator_thenSuccess() {
      Car<Diesel> myCar = new Car<>();
      assertNotNull(myCar);
   }
}
