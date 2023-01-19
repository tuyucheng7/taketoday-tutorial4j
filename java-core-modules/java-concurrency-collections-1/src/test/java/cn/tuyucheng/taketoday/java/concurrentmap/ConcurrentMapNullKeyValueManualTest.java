package cn.tuyucheng.taketoday.java.concurrentmap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConcurrentMapNullKeyValueManualTest {

	private ConcurrentMap<String, Object> concurrentMap;

	@BeforeEach
	void setup() {
		concurrentMap = new ConcurrentHashMap<>();
	}

	@Test
	void givenConcurrentHashMap_whenGetWithNullKey_thenThrowsNPE() {
		assertThrows(NullPointerException.class, () -> concurrentMap.get(null));
	}

	@Test
	void givenConcurrentHashMap_whenGetOrDefaultWithNullKey_thenThrowsNPE() {
		assertThrows(NullPointerException.class, () -> concurrentMap.getOrDefault(null, new Object()));
	}

	@Test
	void givenConcurrentHashMap_whenPutWithNullKey_thenThrowsNPE() {
		assertThrows(NullPointerException.class, () -> concurrentMap.put(null, new Object()));
	}

	@Test
	void givenConcurrentHashMap_whenPutNullValue_thenThrowsNPE() {
		assertThrows(NullPointerException.class, () -> concurrentMap.put("test", null));
	}

	@Test
	void givenConcurrentHashMapAndKeyAbsent_whenPutWithNullKey_thenThrowsNPE() {
		assertThrows(NullPointerException.class, () -> concurrentMap.putIfAbsent(null, new Object()));
	}

	@Test
	void givenConcurrentHashMapAndMapWithNullKey_whenPutNullKeyMap_thenThrowsNPE() {
		Map<String, Object> nullKeyMap = new HashMap<>();
		nullKeyMap.put(null, new Object());
		assertThrows(NullPointerException.class, () -> concurrentMap.putAll(nullKeyMap));
	}

	@Test
	void givenConcurrentHashMapAndMapWithNullValue_whenPutNullValueMap_thenThrowsNPE() {
		Map<String, Object> nullValueMap = new HashMap<>();
		nullValueMap.put("test", null);
		assertThrows(NullPointerException.class, () -> concurrentMap.putAll(nullValueMap));
	}

	@Test
	void givenConcurrentHashMap_whenReplaceNullKeyWithValues_thenThrowsNPE() {
		assertThrows(NullPointerException.class, () -> concurrentMap.replace(null, new Object(), new Object()));
	}

	@Test
	void givenConcurrentHashMap_whenReplaceWithNullNewValue_thenThrowsNPE() {
		Object o = new Object();
		assertThrows(NullPointerException.class, () -> concurrentMap.replace("test", o, null));
	}

	@Test
	void givenConcurrentHashMap_whenReplaceOldNullValue_thenThrowsNPE() {
		Object o = new Object();
		assertThrows(NullPointerException.class, () -> concurrentMap.replace("test", null, o));
	}

	@Test
	void givenConcurrentHashMap_whenReplaceWithNullValue_thenThrowsNPE() {
		assertThrows(NullPointerException.class, () -> concurrentMap.replace("test", null));
	}

	@Test
	void givenConcurrentHashMap_whenReplaceNullKey_thenThrowsNPE() {
		assertThrows(NullPointerException.class, () -> concurrentMap.replace(null, "test"));
	}

	@Test
	void givenConcurrentHashMap_whenReplaceAllMappingNull_thenThrowsNPE() {
		concurrentMap.put("test", new Object());
		assertThrows(NullPointerException.class, () -> concurrentMap.replaceAll((s, o) -> null));
	}

	@Test
	void givenConcurrentHashMap_whenRemoveNullKey_thenThrowsNPE() {
		assertThrows(NullPointerException.class, () -> concurrentMap.remove(null));
	}

	@Test
	void givenConcurrentHashMap_whenRemoveNullKeyWithValue_thenThrowsNPE() {
		assertThrows(NullPointerException.class, () -> concurrentMap.remove(null, new Object()));
	}

	@Test
	void givenConcurrentHashMap_whenMergeNullKeyWithValue_thenThrowsNPE() {
		assertThrows(NullPointerException.class, () -> concurrentMap.merge(null, new Object(), (o, o2) -> o2));
	}

	@Test
	void givenConcurrentHashMap_whenMergeKeyWithNullValue_thenThrowsNPE() {
		concurrentMap.put("test", new Object());
		assertThrows(NullPointerException.class, () -> concurrentMap.merge("test", null, (o, o2) -> o2));
	}

	@Test
	void givenConcurrentHashMapAndAssumeKeyAbsent_whenComputeWithNullKey_thenThrowsNPE() {
		assertThrows(NullPointerException.class, () -> concurrentMap.computeIfAbsent(null, s -> s));
	}

	@Test
	void givenConcurrentHashMapAndAssumeKeyPresent_whenComputeWithNullKey_thenThrowsNPE() {
		assertThrows(NullPointerException.class, () -> concurrentMap.computeIfPresent(null, (s, o) -> o));
	}

	@Test
	void givenConcurrentHashMap_whenComputeWithNullKey_thenThrowsNPE() {
		assertThrows(NullPointerException.class, () -> concurrentMap.compute(null, (s, o) -> o));
	}

	@Test
	void givenConcurrentHashMap_whenMergeKeyRemappingNull_thenRemovesMapping() {
		Object oldValue = new Object();
		concurrentMap.put("test", oldValue);
		concurrentMap.merge("test", new Object(), (o, o2) -> null);

		assertNull(concurrentMap.get("test"));
	}

	@Test
	void givenConcurrentHashMapAndKeyAbsent_whenComputeWithKeyRemappingNull_thenRemainsAbsent() {
		concurrentMap.computeIfPresent("test", (s, o) -> null);
		assertNull(concurrentMap.get("test"));
	}

	@Test
	void givenKeyPresent_whenComputeIfPresentRemappingNull_thenMappingRemoved() {
		Object oldValue = new Object();
		concurrentMap.put("test", oldValue);
		concurrentMap.computeIfPresent("test", (s, o) -> null);
		assertNull(concurrentMap.get("test"));
	}

	@Test
	void givenKeyPresent_whenComputeRemappingNull_thenMappingRemoved() {
		Object oldValue = new Object();
		concurrentMap.put("test", oldValue);
		concurrentMap.compute("test", (s, o) -> null);
		assertNull(concurrentMap.get("test"));
	}
}