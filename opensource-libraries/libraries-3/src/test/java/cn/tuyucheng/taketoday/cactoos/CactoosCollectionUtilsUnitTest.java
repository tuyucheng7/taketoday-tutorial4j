package cn.tuyucheng.taketoday.cactoos;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CactoosCollectionUtilsUnitTest {

	@Test
	public void whenFilteredClassIsCalledWithSpecificArgs_thenCorrespondingFilteredCollectionShouldBeReturned() throws IOException {

		CactoosCollectionUtils obj = new CactoosCollectionUtils();

		// when
		List<String> strings = new ArrayList<String>() {
			{
				add("Hello");
				add("John");
				add("Smith");
				add("Eric");
				add("Dizzy");
			}
		};
		int size = obj.getFilteredList(strings).size();

		// then
		assertEquals(3, size);

	}

}
