package cn.tuyucheng.taketoday.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectsUnitTest {

   private Car car;

   @BeforeEach
   public void setUp() throws Exception {
      car = new Car("Ford", "Focus", "red");
   }

   @Test
   public final void when_speedIncreased_then_verifySpeed() {
      car.increaseSpeed(30);
      assertEquals(30, car.getSpeed());

      car.increaseSpeed(20);
      assertEquals(50, car.getSpeed());
   }

   @Test
   public final void when_speedDecreased_then_verifySpeed() {
      car.increaseSpeed(50);
      assertEquals(50, car.getSpeed());

      car.decreaseSpeed(30);
      assertEquals(20, car.getSpeed());

      car.decreaseSpeed(20);
      assertEquals(0, car.getSpeed());
   }

}
