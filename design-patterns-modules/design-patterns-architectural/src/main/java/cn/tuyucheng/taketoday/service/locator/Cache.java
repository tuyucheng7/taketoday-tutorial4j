package cn.tuyucheng.taketoday.service.locator;

import java.util.ArrayList;
import java.util.List;

public class Cache {
	private List<MessagingService> services;

	public Cache() {
		services = new ArrayList<>();
	}

	public MessagingService getService(String serviceName) {

		for (MessagingService service : services) {
			if (service.getServiceName().equalsIgnoreCase(serviceName)) {
				System.out.println("Returning cached  " + serviceName + " object");
				return service;
			}
		}
		return null;
	}

	public void addService(MessagingService newService) {
		boolean exists = false;

		for (MessagingService service : services) {
			if (service.getServiceName().equalsIgnoreCase(newService.getServiceName())) {
				exists = true;
			}
		}
		if (!exists) {
			services.add(newService);
		}
	}
}