package cn.tuyucheng.taketoday.staticmodifier;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CarUnitTest {
	@Test
	public void whenNumberOfCarObjectsInitialized_thenStaticCounterIncreases() {
		new Car("Jaguar", "V8");
		new Car("Bugatti", "W16");
		assertEquals(2, Car.numberOfCars);
	}
}
