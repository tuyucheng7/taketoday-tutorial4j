package cn.tuyucheng.taketoday.dagger.intro;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Dagger module for providing vehicles components.
 */
@Module
public class VehiclesModule {

	/**
	 * Creates an {@link Engine}.
	 *
	 * @return an {@link Engine}
	 */
	@Provides
	public Engine provideEngine() {
		return new Engine();
	}

	/**
	 * Creates a {@link Brand}.
	 *
	 * @return a {@link Brand}
	 */
	@Provides
	@Singleton
	public Brand provideBrand() {
		return new Brand("Baeldung");
	}
}