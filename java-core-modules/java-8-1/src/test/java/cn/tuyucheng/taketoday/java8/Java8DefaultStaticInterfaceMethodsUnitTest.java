package cn.tuyucheng.taketoday.java8;

import cn.tuyucheng.taketoday.java_8_features.Vehicle;
import cn.tuyucheng.taketoday.java_8_features.VehicleImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Java8DefaultStaticInterfaceMethodsUnitTest {

   @Test
   void callStaticInterfaceMethdosMethods_whenExpectedResults_thenCorrect() {
      Vehicle vehicle = new VehicleImpl();
      String overview = vehicle.getOverview();
      long[] startPosition = vehicle.startPosition();

      assertEquals(overview, "ATV made by N&F Vehicles");
      assertEquals(startPosition[0], 23);
      assertEquals(startPosition[1], 15);
   }

   @Test
   void callDefaultInterfaceMethods_whenExpectedResults_thenCorrect() {
      String producer = Vehicle.producer();
      assertEquals(producer, "N&F Vehicles");
   }
}