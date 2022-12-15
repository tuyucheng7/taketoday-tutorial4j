package cn.tuyucheng.taketoday.singleton.synchronization;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Double-checked locking design pattern applied to a singleton.
 *
 * @author tuyucheng
 */
public class DclSingleton {

	/**
	 * Current instance of the singleton.
	 */
	private static final AtomicReference<DclSingleton> instance = new AtomicReference<>();

	/**
	 * Private constructor to avoid instantiation.
	 */
	private DclSingleton() {
	}

	/**
	 * Returns the current instance of the singleton.
	 *
	 * @return the current instance of the singleton
	 */
	public static DclSingleton getInstance() {
		if (instance.get() == null) {
			synchronized (DclSingleton.class) {
				if (instance.get() == null) {
					instance.set(new DclSingleton());
				}
			}
		}
		return instance.get();
	}
}