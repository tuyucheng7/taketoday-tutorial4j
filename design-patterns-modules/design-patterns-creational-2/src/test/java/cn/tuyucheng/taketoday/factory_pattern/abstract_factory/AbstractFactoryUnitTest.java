package cn.tuyucheng.taketoday.factory_pattern.abstract_factory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractFactoryUnitTest {
	@Test
	void givenFutureVehicleFactory_whenCreateMotorVehicle_thenInstanceOf() {
		Corporation corporation = new FutureVehicleCorporation();
		MotorVehicle motorVehicle = corporation.createMotorVehicle();
		assertThat(motorVehicle).isInstanceOf(MotorVehicle.class).isInstanceOf(FutureVehicleMotorcycle.class);
	}

	@Test
	void givenFutureVehicleFactory_whenCreateElectricVehicle_thenInstanceOf() {
		Corporation corporation = new FutureVehicleCorporation();
		ElectricVehicle electricVehicle = corporation.createElectricVehicle();
		assertThat(electricVehicle).isInstanceOf(ElectricVehicle.class).isInstanceOf(FutureVehicleElectricCar.class);
	}

	@Test
	void givenNextGenFactory_whenCreateMotorVehicle_thenInstanceOf() {
		Corporation corporation = new NextGenCorporation();
		MotorVehicle motorVehicle = corporation.createMotorVehicle();
		assertThat(motorVehicle).isInstanceOf(MotorVehicle.class).isInstanceOf(NextGenMotorcycle.class);
	}

	@Test
	void givenNextGenFactory_whenCreateElectricVehicle_thenInstanceOf() {
		Corporation corporation = new NextGenCorporation();
		ElectricVehicle electricVehicle = corporation.createElectricVehicle();
		assertThat(electricVehicle).isInstanceOf(ElectricVehicle.class).isInstanceOf(NextGenElectricCar.class);
	}
}