package cn.tuyucheng.taketoday.factory_pattern.method;

public class MotorcycleFactory extends MotorVehicleFactory {
	@Override
	protected MotorVehicle createMotorVehicle() {
		return new Motorcycle();
	}
}