package cn.tuyucheng.taketoday.java9.list.immutable;

import com.google.common.collect.ImmutableList;
import org.apache.commons.collections4.ListUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ImmutableArrayListUnitTest {

	@Test
	final void givenUsingTheJdk_whenUnmodifiableListIsCreated_thenNotModifiable() {
		final List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
		final List<String> unmodifiableList = Collections.unmodifiableList(list);
		assertThrows(UnsupportedOperationException.class, () -> unmodifiableList.add("four"));
	}

	@Test
	final void givenUsingTheJava9_whenUnmodifiableListIsCreated_thenNotModifiable() {
		final List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
		final List<String> unmodifiableList = List.of(list.toArray(new String[]{}));
		assertThrows(UnsupportedOperationException.class, () -> unmodifiableList.add("four"));
	}

	@Test
	final void givenUsingGuava_whenUnmodifiableListIsCreated_thenNotModifiable() {
		final List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
		final List<String> unmodifiableList = ImmutableList.copyOf(list);
		assertThrows(UnsupportedOperationException.class, () -> unmodifiableList.add("four"));
	}

	@Test
	final void givenUsingGuavaBuilder_whenUnmodifiableListIsCreated_thenNoLongerModifiable() {
		final List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
		final ImmutableList<String> unmodifiableList = ImmutableList.<String>builder().addAll(list).build();
		assertThrows(UnsupportedOperationException.class, () -> unmodifiableList.add("four"));
	}

	@Test
	final void givenUsingCommonsCollections_whenUnmodifiableListIsCreated_thenNotModifiable() {
		final List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
		final List<String> unmodifiableList = ListUtils.unmodifiableList(list);
		assertThrows(UnsupportedOperationException.class, () -> unmodifiableList.add("four"));
	}
}