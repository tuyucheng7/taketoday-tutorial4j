package cn.tuyucheng.taketoday.java9.language.collections;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MapFactoryMethodsUnitTest {

	@Test
	void whenMapCreated_thenSuccess() {
		Map<String, String> traditionlMap = new HashMap<>();
		traditionlMap.put("foo", "a");
		traditionlMap.put("bar", "b");
		traditionlMap.put("baz", "c");
		Map<String, String> factoryCreatedMap = Map.of("foo", "a", "bar", "b", "baz", "c");
		assertEquals(traditionlMap, factoryCreatedMap);
	}

	@Test
	void onElemAdd_ifUnSupportedOpExpnThrown_thenSuccess() {
		Map<String, String> map = Map.of("foo", "a", "bar", "b");
		assertThrows(UnsupportedOperationException.class, () -> map.put("baz", "c"));
	}

	@Test
	void onElemModify_ifUnSupportedOpExpnThrown_thenSuccess() {
		Map<String, String> map = Map.of("foo", "a", "bar", "b");
		assertThrows(UnsupportedOperationException.class, () -> map.put("foo", "c"));
	}

	@Test
	void onElemRemove_ifUnSupportedOpExpnThrown_thenSuccess() {
		Map<String, String> map = Map.of("foo", "a", "bar", "b");
		assertThrows(UnsupportedOperationException.class, () -> map.remove("foo"));
	}

	@Test
	void givenDuplicateKeys_ifIllegalArgExp_thenSuccess() {
		assertThrows(IllegalArgumentException.class, () -> Map.of("foo", "a", "foo", "b"));
	}

	@Test
	void onNullKey_ifNullPtrExp_thenSuccess() {
		assertThrows(NullPointerException.class, () -> Map.of("foo", "a", null, "b"));
	}

	@Test
	void onNullValue_ifNullPtrExp_thenSuccess() {
		assertThrows(NullPointerException.class, () -> Map.of("foo", "a", "bar", null));
	}

	@Test
	void ifNotHashMap_thenSuccess() {
		Map<String, String> map = Map.of("foo", "a", "bar", "b");
		assertFalse(map instanceof HashMap);
	}
}