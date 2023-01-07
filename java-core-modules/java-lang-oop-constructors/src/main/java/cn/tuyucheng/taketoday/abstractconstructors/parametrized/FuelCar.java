package cn.tuyucheng.taketoday.abstractconstructors.parametrized;

public class FuelCar extends Car {

	String fuel;

	public FuelCar(String fuel) {
		this.fuel = fuel;
	}

	@Override
	String getInformation() {
		return new StringBuilder("Fuel Car")
			.append("\nFuel type: " + fuel)
			.toString();
	}
}
