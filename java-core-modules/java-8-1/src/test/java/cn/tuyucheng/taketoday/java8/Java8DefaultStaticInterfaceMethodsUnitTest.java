package cn.tuyucheng.taketoday.java8;

import cn.tuyucheng.taketoday.java_8_features.Vehicle;
import cn.tuyucheng.taketoday.java_8_features.VehicleImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Java8DefaultStaticInterfaceMethodsUnitTest {

	@Test
	void callStaticInterfaceMethodsMethods_whenExpectedResults_thenCorrect() {
		Vehicle vehicle = new VehicleImpl();
		String overview = vehicle.getOverview();
		long[] startPosition = vehicle.startPosition();

		assertEquals("ATV made by N&F Vehicles", overview);
		assertEquals(23, startPosition[0]);
		assertEquals(15, startPosition[1]);
	}

	@Test
	void callDefaultInterfaceMethods_whenExpectedResults_thenCorrect() {
		String producer = Vehicle.producer();
		assertEquals("N&F Vehicles", producer);
	}
}