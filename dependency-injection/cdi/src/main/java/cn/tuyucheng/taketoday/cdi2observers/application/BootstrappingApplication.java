package cn.tuyucheng.taketoday.cdi2observers.application;

import cn.tuyucheng.taketoday.cdi2observers.events.ExampleEvent;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

public class BootstrappingApplication {

	public static void main(String... args) {
		SeContainerInitializer containerInitializer = SeContainerInitializer.newInstance();
		try (SeContainer container = containerInitializer.initialize()) {
			container.getBeanManager().fireEvent(new ExampleEvent("Welcome to Tuyucheng!"));
		}
	}
}
