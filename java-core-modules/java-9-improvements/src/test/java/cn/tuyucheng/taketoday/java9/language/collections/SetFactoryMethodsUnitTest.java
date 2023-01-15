package cn.tuyucheng.taketoday.java9.language.collections;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SetFactoryMethodsUnitTest {

	@Test
	void whenSetCreated_thenSuccess() {
		Set<String> traditionlSet = new HashSet<String>();
		traditionlSet.add("foo");
		traditionlSet.add("bar");
		traditionlSet.add("baz");
		Set<String> factoryCreatedSet = Set.of("foo", "bar", "baz");
		assertEquals(traditionlSet, factoryCreatedSet);
	}

	@Test
	void onDuplicateElem_IfIllegalArgExp_thenSuccess() {
		assertThrows(IllegalArgumentException.class, () -> Set.of("foo", "bar", "baz", "foo"));
	}

	@Test
	void onElemAdd_ifUnSupportedOpExpnThrown_thenSuccess() {
		Set<String> set = Set.of("foo", "bar");
		assertThrows(UnsupportedOperationException.class, () -> set.add("baz"));
	}

	@Test
	void onElemRemove_ifUnSupportedOpExpnThrown_thenSuccess() {
		Set<String> set = Set.of("foo", "bar", "baz");
		assertThrows(UnsupportedOperationException.class, () -> set.remove("foo"));
	}

	@Test
	void onNullElem_ifNullPtrExpnThrown_thenSuccess() {
		assertThrows(NullPointerException.class, () -> Set.of("foo", "bar", null));
	}

	@Test
	void ifNotHashSet_thenSuccess() {
		Set<String> list = Set.of("foo", "bar");
		assertFalse(list instanceof HashSet);
	}

	@Test
	void ifSetSizeIsOne_thenSuccess() {
		int[] arr = {1, 2, 3, 4};
		Set<int[]> set = Set.of(arr);
		assertEquals(1, set.size());
		assertArrayEquals(arr, set.iterator().next());
	}
}