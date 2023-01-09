package cn.tuyucheng.taketoday.dagger.intro;

import dagger.Component;

import javax.inject.Singleton;

/**
 * Dagger component for building vehicles.
 */
@Singleton
@Component(modules = VehiclesModule.class)
public interface VehiclesComponent {

	/**
	 * Builds a {@link Car}.
	 *
	 * @return a {@link Car}
	 */
	public Car buildCar();

}