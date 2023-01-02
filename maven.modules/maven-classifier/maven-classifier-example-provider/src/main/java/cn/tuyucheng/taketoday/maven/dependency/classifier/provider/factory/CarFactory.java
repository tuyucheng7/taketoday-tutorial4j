package cn.tuyucheng.taketoday.maven.dependency.classifier.provider.factory;

import cn.tuyucheng.taketoday.maven.dependency.classifier.provider.model.Car;
import cn.tuyucheng.taketoday.maven.dependency.classifier.provider.model.Car.Type;

public class CarFactory {

	public static Car manufacture(Type carType) {
		Car car = new Car();
		car.setType(carType);

		return car;
	}
}