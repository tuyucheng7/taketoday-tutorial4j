package cn.tuyucheng.taketoday.factory_pattern.method;

public class CarFactory extends MotorVehicleFactory {
	@Override
	protected MotorVehicle createMotorVehicle() {
		return new Car();
	}
}