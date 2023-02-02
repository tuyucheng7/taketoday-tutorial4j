package cn.tuyucheng.taketoday.sealed.classes;

public abstract sealed class Vehicle permits Car, Truck {

	protected final String registrationNumber;

	public Vehicle(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}
}