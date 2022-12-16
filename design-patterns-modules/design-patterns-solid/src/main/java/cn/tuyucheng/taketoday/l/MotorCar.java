package cn.tuyucheng.taketoday.l;

public class MotorCar implements Car {

	private Engine engine;

	public MotorCar(Engine engine) {
		this.engine = engine;
	}

	public void turnOnEngine() {
		// turn on the engine!
		engine.on();
	}

	public void accelerate() {
		// move forward!
		engine.powerOn(1000);
	}
}