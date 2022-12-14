package cn.tuyucheng.taketoday.constructorsstaticfactorymethods;

import cn.tuyucheng.taketoday.constructorsstaticfactorymethods.entities.User;

public class Application {

	public static void main(String[] args) {
		User user1 = User.createWithDefaultCountry("John", "john@domain.com");
		User user2 = User.createWithLoggedInstantiationTime("John", "john@domain.com", "Argentina");
		User user3 = User.getSingletonInstance("John", "john@domain.com", "Argentina");
	}
}