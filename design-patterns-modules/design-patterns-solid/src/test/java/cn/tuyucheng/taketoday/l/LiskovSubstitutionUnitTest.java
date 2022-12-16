package cn.tuyucheng.taketoday.l;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LiskovSubstitutionUnitTest {

	@Test
	void whenCreateCarOfMotor_thenShouldAllCreateEngine() {
		Engine engine = new Engine();
		Car car = new MotorCar(engine);

		car.turnOnEngine();
		car.accelerate();
	}

	@Test
	void whenCreateCarOfElectric_thenShouldNotCreateEngine() {
		Car car = new ElectricCar();

		assertThrows(AssertionError.class, car::turnOnEngine);
		car.accelerate();
	}
}