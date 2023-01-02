package cn.tuyucheng.taketoday.maven.classifier.consumer;

import cn.tuyucheng.taketoday.maven.classifier.consumer.FuelStation.Zone;
import cn.tuyucheng.taketoday.maven.dependency.classifier.provider.model.Car;
import cn.tuyucheng.taketoday.maven.dependency.classifier.provider.stub.CarStub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FuelStationUnitTest {

	@Test
	@DisplayName("Given fuel type battery When request for refill Then Return Battery Zone")
	void givenFuelTypeBattery_whenRequestToRefill_thenReturnBatteryZone() {
		FuelStation fuelStation = new FuelStation();
		Car electricCar = CarStub.ELECTRIC_CAR;

		assertEquals(Zone.BATTERY, fuelStation.refill(electricCar));
	}
}