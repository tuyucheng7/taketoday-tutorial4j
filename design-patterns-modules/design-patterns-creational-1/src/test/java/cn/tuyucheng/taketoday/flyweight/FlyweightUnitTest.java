package cn.tuyucheng.taketoday.flyweight;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit test for {@link VehicleFactory}.
 *
 * @author tuyucheng
 */
class FlyweightUnitTest {

	/**
	 * Checks that when the {@link VehicleFactory} is asked to provide two
	 * vehicles of different colors, the objects returned are different.
	 */
	@Test
	void givenDifferentFlyweightObjects_whenEquals_thenFalse() {
		Vehicle blackVehicle = VehicleFactory.createVehicle(Color.BLACK);
		Vehicle blueVehicle = VehicleFactory.createVehicle(Color.BLUE);
		blackVehicle.start();

		assertNotNull(blackVehicle, "Object returned by the factory is null!");
		assertNotNull(blueVehicle, "Object returned by the factory is null!");
		assertEquals(Color.BLACK, blackVehicle.getColor(), "The color of the black vehicle is not black!");
		assertNotEquals(blackVehicle, blueVehicle, "Objects returned by the factory are equals!");

		blackVehicle.stop();
	}

	/**
	 * Checks that when the {@link VehicleFactory} is asked to provide two
	 * vehicles of the same colors, the same object is returned twice.
	 */
	@Test
	void givenSameFlyweightObjects_whenEquals_thenTrue() {
		Vehicle blackVehicle = VehicleFactory.createVehicle(Color.BLACK);
		Vehicle anotherBlackVehicle = VehicleFactory.createVehicle(Color.BLACK);

		assertNotNull(blackVehicle, "Object returned by the factory is null!");
		assertNotNull(anotherBlackVehicle, "Object returned by the factory is null!");
		assertEquals(blackVehicle, anotherBlackVehicle, "Objects returned by the factory are not equals!");
	}
}