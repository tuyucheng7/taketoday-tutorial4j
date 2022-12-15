package cn.tuyucheng.taketoday.constructorsstaticfactorymethods.entities;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {

	private static final AtomicReference<User> instance = new AtomicReference<>();
	private static final Logger LOGGER = Logger.getLogger(User.class.getName());
	private final String name;
	private final String email;
	private final String country;

	public static User createWithDefaultCountry(String name, String email) {
		return new User(name, email, "Argentina");
	}

	public static User createWithLoggedInstantiationTime(String name, String email, String country) {
		LOGGER.log(Level.INFO, "Creating User instance at : {0}", LocalTime.now());

		return new User(name, email, country);
	}

	public static User getSingletonInstance(String name, String email, String country) {
		if (instance.get() ==  null) {
			synchronized (User.class) {
				if (instance.get() == null) {
					instance.set(new User(name, email, country));
				}
			}
		}
		return instance.get();
	}

	private User(String name, String email, String country) {
		this.name = name;
		this.email = email;
		this.country = country;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getCountry() {
		return country;
	}
}