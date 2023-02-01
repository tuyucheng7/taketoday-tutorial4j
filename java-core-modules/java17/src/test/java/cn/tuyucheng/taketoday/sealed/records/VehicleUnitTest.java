package cn.tuyucheng.taketoday.sealed.records;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleUnitTest {

	private static Vehicle car;
	private static Vehicle truck;

	@BeforeAll
	static void createInstances() {
		car = new Car(4, "VZ500DA");
		truck = new Truck(16000, "VZ600TA");
	}

	@Test
	void givenCar_whenUsingReflectionAPI_thenInterfaceIsSealed() {
		assertThat(car.getClass().isSealed()).isEqualTo(false);
		assertThat(car.getClass().getInterfaces()[0].isSealed()).isEqualTo(true);
		assertThat(Arrays.stream(car.getClass().getInterfaces()[0].getPermittedSubclasses()).map(Class::getName).toList())
			.contains(car.getClass().getCanonicalName());
	}

	@Test
	void givenTruck_whenUsingReflectionAPI_thenInterfaceIsSealed() {
		assertThat(truck.getClass().isSealed()).isEqualTo(false);
		assertThat(truck.getClass().getInterfaces()[0].isSealed()).isEqualTo(true);
		assertThat(Arrays.stream(truck.getClass().getInterfaces()[0].getPermittedSubclasses()).map(Class::getName).toList())
			.contains(truck.getClass().getCanonicalName());
	}

	@Test
	void givenCar_whenGettingPropertyTraditionalWay_thenNumberOfSeatsPropertyIsReturned() {
		assertThat(getPropertyTraditionalWay(car)).isEqualTo(4);
	}

	@Test
	void givenCar_whenGettingPropertyViaPatternMatching_thenNumberOfSeatsPropertyIsReturned() {
		assertThat(getPropertyViaPatternMatching(car)).isEqualTo(4);
	}

	@Test
	void givenTruck_whenGettingPropertyTraditionalWay_thenLoadCapacityIsReturned() {
		assertThat(getPropertyTraditionalWay(truck)).isEqualTo(16000);
	}

	@Test
	void givenTruck_whenGettingPropertyViaPatternMatching_thenLoadCapacityIsReturned() {
		assertThat(getPropertyViaPatternMatching(truck)).isEqualTo(16000);
	}

	private int getPropertyTraditionalWay(Vehicle vehicle) {
		if (vehicle instanceof Car) {
			return ((Car) vehicle).getNumberOfSeats();
		} else if (vehicle instanceof Truck) {
			return ((Truck) vehicle).getLoadCapacity();
		} else {
			throw new RuntimeException("Unknown instance of Vehicle");
		}
	}

	private int getPropertyViaPatternMatching(Vehicle vehicle) {
		if (vehicle instanceof Car car) {
			return car.getNumberOfSeats();
		} else if (vehicle instanceof Truck truck) {
			return truck.getLoadCapacity();
		} else {
			throw new RuntimeException("Unknown instance of Vehicle");
		}
	}
}