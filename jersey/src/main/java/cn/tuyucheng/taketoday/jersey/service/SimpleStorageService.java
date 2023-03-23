package cn.tuyucheng.taketoday.jersey.service;

import java.util.HashMap;
import java.util.Map;

import cn.tuyucheng.taketoday.jersey.server.model.Fruit;

public class SimpleStorageService {

	private static final Map<String, Fruit> fruits = new HashMap<>();

	public static void storeFruit(final Fruit fruit) {
		fruits.put(fruit.getName(), fruit);
	}

	public static Fruit findByName(final String name) {
		return fruits.entrySet()
			.stream()
			.filter(map -> name.equals(map.getKey()))
			.map(Map.Entry::getValue)
			.findFirst()
			.get();
	}

}
