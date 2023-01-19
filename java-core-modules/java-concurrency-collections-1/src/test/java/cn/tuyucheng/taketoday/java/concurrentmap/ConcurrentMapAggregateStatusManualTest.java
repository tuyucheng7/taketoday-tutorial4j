package cn.tuyucheng.taketoday.java.concurrentmap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ConcurrentMapAggregateStatusManualTest {
	private ExecutorService executorService;
	private Map<String, Integer> concurrentMap;
	private List<Integer> mapSizes;
	private final int MAX_SIZE = 100000;

	@BeforeEach
	void init() {
		executorService = Executors.newFixedThreadPool(2);
		concurrentMap = new ConcurrentHashMap<>();
		mapSizes = new ArrayList<>(MAX_SIZE);
	}

	@Test
	void givenConcurrentMap_whenSizeWithoutConcurrentUpdates_thenCorrect() throws InterruptedException {
		Runnable collectMapSizes = () -> {
			for (int i = 0; i < MAX_SIZE; i++) {
				concurrentMap.put(String.valueOf(i), i);
				mapSizes.add(concurrentMap.size());
			}
		};
		Runnable retrieveMapData = () -> {
			for (int i = 0; i < MAX_SIZE; i++) {
				concurrentMap.get(String.valueOf(i));
			}
		};
		executorService.execute(retrieveMapData);
		executorService.execute(collectMapSizes);
		executorService.shutdown();
		executorService.awaitTermination(1, TimeUnit.MINUTES);

		for (int i = 1; i <= MAX_SIZE; i++) {
			assertEquals(i, mapSizes.get(i - 1).intValue(), "map size should be consistently reliable");
		}
		assertEquals(MAX_SIZE, concurrentMap.size());
	}

	@Test
	void givenConcurrentMap_whenUpdatingAndGetSize_thenError() throws InterruptedException {
		Runnable collectMapSizes = () -> {
			for (int i = 0; i < MAX_SIZE; i++) {
				mapSizes.add(concurrentMap.size());
			}
		};
		Runnable updateMapData = () -> {
			for (int i = 0; i < MAX_SIZE; i++) {
				concurrentMap.put(String.valueOf(i), i);
			}
		};
		executorService.execute(updateMapData);
		executorService.execute(collectMapSizes);
		executorService.shutdown();
		executorService.awaitTermination(1, TimeUnit.MINUTES);

		assertNotEquals(MAX_SIZE, mapSizes.get(MAX_SIZE - 1).intValue(), "map size collected with concurrent updates not reliable");
		assertEquals(MAX_SIZE, concurrentMap.size());
	}
}