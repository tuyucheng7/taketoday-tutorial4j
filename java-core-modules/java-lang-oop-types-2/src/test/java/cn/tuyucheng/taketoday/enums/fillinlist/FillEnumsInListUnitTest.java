package cn.tuyucheng.taketoday.enums.fillinlist;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static cn.tuyucheng.taketoday.enums.fillinlist.MagicNumber.FIVE;
import static cn.tuyucheng.taketoday.enums.fillinlist.MagicNumber.FOUR;
import static cn.tuyucheng.taketoday.enums.fillinlist.MagicNumber.ONE;
import static cn.tuyucheng.taketoday.enums.fillinlist.MagicNumber.THREE;
import static cn.tuyucheng.taketoday.enums.fillinlist.MagicNumber.TWO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

enum MagicNumber {
	ONE, TWO, THREE, FOUR, FIVE
}

public class FillEnumsInListUnitTest {
	private static final List<MagicNumber> EXPECTED_LIST = Arrays.asList(ONE, TWO, THREE, FOUR, FIVE);

	static <T> List<T> enumValuesInList(Class<T> enumCls) {
		T[] arr = enumCls.getEnumConstants();
		return arr == null ? Collections.emptyList() : Arrays.asList(arr);
	}

	@Test
	void givenEnum_whenUsingArraysAsList_shouldGetExpectedResult() {
		List<MagicNumber> result = Arrays.asList(MagicNumber.values());
		assertEquals(EXPECTED_LIST, result);
	}

	@Test
	void givenEnum_whenUsingGetEnumConstants_shouldGetExpectedResult() {
		List<MagicNumber> result = Arrays.asList(MagicNumber.class.getEnumConstants());
		assertEquals(EXPECTED_LIST, result);
	}

	@Test
	void givenEnum_whenUsingenumValuesInList_shouldGetExpectedResult() {
		List<MagicNumber> result1 = enumValuesInList(MagicNumber.class);
		assertEquals(EXPECTED_LIST, result1);

		List<Integer> result2 = enumValuesInList(Integer.class);
		assertTrue(result2.isEmpty());
	}

	@Test
	void givenEnum_whenUsingEnumSet_shouldGetExpectedResult() {
		List<MagicNumber> result = new ArrayList<>(EnumSet.allOf(MagicNumber.class));
		assertEquals(EXPECTED_LIST, result);
	}

}
