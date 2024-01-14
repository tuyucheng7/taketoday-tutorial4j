package cn.tuyucheng.taketoday.staticmethod;

import cn.tuyucheng.taketoday.staticmodifier.Car;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CallNonStaticMethodUnitTest {
   @AfterAll
   public static void setUpCarInstance() {
      Car.setNumberOfCars(0);
   }

   @Test
   public void whenCallingNonStaticMethodInStaticMethodWithInstanceClass_thenSuccess() {
      Car car = new Car("Jaguar", "V8");
      assertEquals("Jaguar-V8", Car.getCarsInformation(car));
   }

}
