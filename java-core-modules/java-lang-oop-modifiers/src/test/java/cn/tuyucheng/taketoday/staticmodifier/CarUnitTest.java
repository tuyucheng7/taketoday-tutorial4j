package cn.tuyucheng.taketoday.staticmodifier;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CarUnitTest {

   @AfterAll
   public static void setUpCarInstance() {
      Car.setNumberOfCars(0);
   }

   @Test
   public void whenNumberOfCarObjectsInitialized_thenStaticCounterIncreases() {
      new Car("Jaguar", "V8");
      new Car("Bugatti", "W16");
      assertEquals(2, Car.numberOfCars);
   }
}
