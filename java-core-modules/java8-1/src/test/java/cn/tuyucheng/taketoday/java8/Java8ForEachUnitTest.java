package cn.tuyucheng.taketoday.java8;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

class Java8ForEachUnitTest {

	private static final Logger LOG = LoggerFactory.getLogger(Java8ForEachUnitTest.class);

	@Test
	void compareForEachMethods_thenPrintResults() {
		List<String> names = new ArrayList<>();
		names.add("Larry");
		names.add("Steve");
		names.add("James");
		names.add("Conan");
		names.add("Ellen");

		// Java 5 - for-loop
		LOG.debug("--- Enhanced for-loop ---");
		for (String name : names) {
			LOG.debug(name);
		}

		// Java 8 - forEach
		names.forEach(System.out::println);

		LOG.debug("--- Print Consumer ---");
		Consumer<String> printConsumer = new Consumer<String>() {
			public void accept(String name) {
				System.out.println(name);
			}
		};

		names.forEach(printConsumer);

		// Anonymous inner class that implements Consumer interface
		LOG.debug("--- Anonymous inner class ---");
		names.forEach(new Consumer<String>() {
			public void accept(String name) {
				LOG.debug(name);
			}
		});

		// Java 8 - forEach - Lambda Syntax
		LOG.debug("--- forEach method ---");
		names.forEach(name -> LOG.debug(name));

		// Java 8 - forEach - Print elements using a Method Reference
		LOG.debug("--- Method Reference ---");
		names.forEach(LOG::debug);
	}

	@Test
	void givenList_thenIterateAndPrintResults() {
		List<String> names = Arrays.asList("Larry", "Steve", "James");

		names.forEach(System.out::println);
	}

	@Test
	void givenSet_thenIterateAndPrintResults() {
		Set<String> uniqueNames = new HashSet<>(Arrays.asList("Larry", "Steve", "James"));

		uniqueNames.forEach(System.out::println);
	}

	@Test
	void givenQueue_thenIterateAndPrintResults() {
		Queue<String> namesQueue = new ArrayDeque<>(Arrays.asList("Larry", "Steve", "James"));

		namesQueue.forEach(System.out::println);
	}

	@Test
	void givenMap_thenIterateAndPrintResults() {
		Map<Integer, String> namesMap = new HashMap<>();
		namesMap.put(1, "Larry");
		namesMap.put(2, "Steve");
		namesMap.put(3, "James");

		namesMap.forEach((key, value) -> System.out.println(key + " " + value));
	}

	@Test
	void givenMap_whenUsingBiConsumer_thenIterateAndPrintResults2() {
		Map<Integer, String> namesMap = new HashMap<>();
		namesMap.put(1, "Larry");
		namesMap.put(2, "Steve");
		namesMap.put(3, "James");

		namesMap.forEach((key, value) -> System.out.println(key + " " + value));
	}
}