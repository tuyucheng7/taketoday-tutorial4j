package cn.tuyucheng.taketoday.factory_pattern.method;

public abstract class MotorVehicleFactory {
	public MotorVehicle create() {
		return createMotorVehicle();
	}

	protected abstract MotorVehicle createMotorVehicle();
}