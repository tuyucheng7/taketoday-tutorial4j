package cn.tuyucheng.taketoday.interfacevsabstractclass;

import org.junit.jupiter.api.Test;

class VehicleUnitTest {

	@Test
	void givenVehicle_whenNeedToDrive_thenStart() {
		Vehicle car = new Car("BMW");

		car.start();
		car.drive();
		car.changeGear();
		car.stop();
	}

}
