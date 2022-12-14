package cn.tuyucheng.taketoday.factory_pattern.method;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FactoryMethodUnitTest {
	@Test
	void givenCarFactory_whenCreateMotorVehicle_thenInstanceOf() {
		MotorVehicleFactory factory = new CarFactory();
		MotorVehicle car = factory.createMotorVehicle();
		assertThat(car).isInstanceOf(MotorVehicle.class).isInstanceOf(Car.class);
	}

	@Test
	void givenMotorcycleFactory_whenCreateMotorVehicle_thenInstanceOf() {
		MotorVehicleFactory factory = new MotorcycleFactory();
		MotorVehicle motorcycle = factory.createMotorVehicle();
		assertThat(motorcycle).isInstanceOf(MotorVehicle.class).isInstanceOf(Motorcycle.class);
	}

	@Test
	void givenMotorcycleFactory_whenCreateIsCalled_thenInstanceOf() {
		MotorVehicleFactory factory = new MotorcycleFactory();
		MotorVehicle motorcycle = factory.create();
		assertThat(motorcycle).isInstanceOf(MotorVehicle.class).isInstanceOf(Motorcycle.class);
	}
}