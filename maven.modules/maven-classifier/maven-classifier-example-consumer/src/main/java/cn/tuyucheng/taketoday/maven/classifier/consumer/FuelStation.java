package cn.tuyucheng.taketoday.maven.classifier.consumer;

import cn.tuyucheng.taketoday.maven.dependency.classifier.provider.model.Car;
import cn.tuyucheng.taketoday.maven.dependency.classifier.provider.model.PowerSource;

public class FuelStation {

	public FuelStation.Zone refill(Car car) {
		return PowerSource.BATTERY.equals(car.getPowerSource()) ? FuelStation.Zone.BATTERY : FuelStation.Zone.UNKNOWN;
	}

	public enum Zone {
		BATTERY, UNKNOWN
	}
}