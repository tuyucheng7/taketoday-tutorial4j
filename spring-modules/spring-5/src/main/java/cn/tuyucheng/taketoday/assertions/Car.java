package cn.tuyucheng.taketoday.assertions;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Car {
	private String state = "stop";

	public void drive(int speed) {
		Assert.isTrue(speed > 0, "speed must be positive");
		this.state = "drive";
		// ...
		if (!(speed > 0)) {
			throw new IllegalArgumentException("speed must be >0");
		}
	}

	public void stop() {
		this.state = "stop";
	}

	public void fuel() {
		Assert.state(this.state.equals("stop"), "car must be stopped");
		// ...
	}

	public void fuelwithSupplier() {
		Assert.state(this.state.equals("stop"), () -> "car must be stopped");
		// ...
	}

	public void changeOil(String oil) {
		Assert.notNull(oil, "oil mustn't be null");
		// ...
	}

	public void replaceBattery(CarBattery carBattery) {
		Assert.isNull(carBattery.getCharge(), "to replace battery the charge must be null");
		// ...
	}

	public void changeEngine(Engine engine) {
		Assert.isInstanceOf(ToyotaEngine.class, engine);
		// ...
	}

	public void repairEngine(Engine engine) {
		Assert.isAssignable(Engine.class, ToyotaEngine.class);
		// ...
	}

	public void startWithHasLength(String key) {
		Assert.hasLength(key, "key must not be null and must not the empty");
		// ...
	}

	public void startWithHasText(String key) {
		Assert.hasText(key, "key must not be null and must contain at least one non-whitespace  character");
		// ...
	}

	public void startWithNotContain(String key) {
		Assert.doesNotContain(key, "123", "key must not contain 123");
		// ...
	}

	public void repair(Collection<String> repairParts) {
		Assert.notEmpty(repairParts, "collection of repairParts mustn't be empty");
		// ...
	}

	public void repair(Map<String, String> repairParts) {
		Assert.notEmpty(repairParts, "map of repairParts mustn't be empty");
		// ...
	}

	public void repair(String[] repairParts) {
		Assert.notEmpty(repairParts, "array of repairParts must not be empty");
		// ...
	}

	public void repairWithNoNull(String[] repairParts) {
		Assert.noNullElements(repairParts, "array of repairParts must not contain null elements");
		// ...
	}

	public static void main(String[] args) {
		Car car = new Car();

		car.drive(50);

		car.stop();

		car.fuel();

		car.changeOil("oil");

		CarBattery carBattery = new CarBattery();
		car.replaceBattery(carBattery);

		car.changeEngine(new ToyotaEngine());

		car.startWithHasLength(" ");
		car.startWithHasText("t");
		car.startWithNotContain("132");

		List<String> repairPartsCollection = new ArrayList<>();
		repairPartsCollection.add("part");
		car.repair(repairPartsCollection);

		Map<String, String> repairPartsMap = new HashMap<>();
		repairPartsMap.put("1", "part");
		car.repair(repairPartsMap);

		String[] repairPartsArray = {"part"};
		car.repair(repairPartsArray);
	}
}