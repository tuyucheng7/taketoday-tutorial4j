package cn.tuyucheng.taketoday.java9.language.collections;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ListFactoryMethodsUnitTest {


	@Test
	void whenListCreated_thenSuccess() {
		List<String> traditionlList = new ArrayList<>();
		traditionlList.add("foo");
		traditionlList.add("bar");
		traditionlList.add("baz");
		List<String> factoryCreatedList = List.of("foo", "bar", "baz");

		assertEquals(traditionlList, factoryCreatedList);
	}

	@Test
	void onDuplicateElem_IfIllegalArgExp_thenSuccess() {
		assertThrows(IllegalArgumentException.class, () -> Set.of("foo", "bar", "baz", "foo"));
	}

	@Test
	void onElemAdd_ifUnSupportedOpExpnThrown_thenSuccess() {
		List<String> list = List.of("foo", "bar");
		assertThrows(UnsupportedOperationException.class, () -> list.add("baz"));
	}

	@Test
	void onElemModify_ifUnSupportedOpExpnThrown_thenSuccess() {
		List<String> list = List.of("foo", "bar");
		assertThrows(UnsupportedOperationException.class, () -> list.set(0, "baz"));
	}

	@Test
	void onElemRemove_ifUnSupportedOpExpnThrown_thenSuccess() {
		List<String> list = List.of("foo", "bar");
		assertThrows(UnsupportedOperationException.class, () -> list.remove("foo"));
	}

	@Test
	void onNullElem_ifNullPtrExpnThrown_thenSuccess() {
		assertThrows(NullPointerException.class, () -> List.of("foo", "bar", null));
	}

	@Test
	void ifNotArrayList_thenSuccess() {
		List<String> list = List.of("foo", "bar");
		assertFalse(list instanceof ArrayList);
	}

	@Test
	void ifListSizeIsOne_thenSuccess() {
		int[] arr = {1, 2, 3, 4};
		List<int[]> list = List.of(arr);
		assertEquals(1, list.size());
		assertArrayEquals(arr, list.get(0));
	}
}