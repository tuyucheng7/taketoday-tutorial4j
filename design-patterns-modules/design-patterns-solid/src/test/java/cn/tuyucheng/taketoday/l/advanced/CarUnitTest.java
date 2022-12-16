package cn.tuyucheng.taketoday.l.advanced;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CarUnitTest {

	@Test
	void whenCreateElectricCar_thenShouldBeCreated() {
		Car electricCar = new ElectricCar(100);

		electricCar.accelerate();
		electricCar.brake();
		assertThrows(AssertionError.class, electricCar::turnOnEngine);
	}

	@Test
	void whenCreateMotorCar_thenShouldBeCreated() {
		Car gasolineCar = new MotorCar(100);

		gasolineCar.accelerate();
		gasolineCar.brake();
		gasolineCar.turnOnEngine();
	}

	@Test
	void whenCreateHybridCar_thenShouldBeCreated() {
		Car hybridCar = new HybridCar(100);

		hybridCar.accelerate();
		hybridCar.brake();
		hybridCar.turnOnEngine();
	}

	@Test
	void whenCreateToyCar_thenShouldBeCreated() {
		ToyCar toyCar = new ToyCar(100);

		toyCar.accelerate();
		toyCar.brake();
		toyCar.turnOnEngine();
		toyCar.reset();
	}
}