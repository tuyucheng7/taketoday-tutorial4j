package cn.tuyucheng.taketoday.abstractconstructors.parametrized;

public class ElectricCar extends Car {

	int chargingTime;

	public ElectricCar(int chargingTime) {
		this.chargingTime = chargingTime;
	}

	@Override
	String getInformation() {
		return new StringBuilder("Electric Car")
			.append("\nCharging Time: " + chargingTime)
			.toString();
	}
}
