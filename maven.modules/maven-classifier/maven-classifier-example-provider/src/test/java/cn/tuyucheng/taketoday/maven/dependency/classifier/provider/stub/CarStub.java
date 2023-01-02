package cn.tuyucheng.taketoday.maven.dependency.classifier.provider.stub;

import cn.tuyucheng.taketoday.maven.dependency.classifier.provider.model.Car;
import cn.tuyucheng.taketoday.maven.dependency.classifier.provider.model.Car.Type;
import cn.tuyucheng.taketoday.maven.dependency.classifier.provider.model.PowerSource;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class CarStub {
	public static Car ELECTRIC_CAR = Mockito.mock(Car.class);

	static {
		when(ELECTRIC_CAR.getType()).thenReturn(Type.ELECTRIC);
		when(ELECTRIC_CAR.getPowerSource()).thenReturn(PowerSource.BATTERY);
	}
}