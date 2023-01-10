package cn.tuyucheng.taketoday.distinct;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static cn.tuyucheng.taketoday.distinct.DistinctWithJavaFunction.distinctByKey;
import static org.junit.Assert.assertTrue;

public class DistinctWithJavaFunctionUnitTest {
	List<Person> personList;

	@Before
	public void init() {
		personList = PersonDataGenerator.getPersonListWithFakeValues();
	}

	@Test
	public void whenFilterListByName_thenSizeShouldBe4() {
		List<Person> personListFiltered = personList.stream().filter(distinctByKey(p -> p.getName())).collect(Collectors.toList());
		assertTrue(personListFiltered.size() == 4);
	}

	@Test
	public void whenFilterListByAge_thenSizeShouldBe2() {
		List<Person> personListFiltered = personList.stream().filter(distinctByKey(p -> p.getAge())).collect(Collectors.toList());
		assertTrue(personListFiltered.size() == 2);
	}

	@Test
	public void whenFilterListWithDefaultDistinct_thenSizeShouldBe5() {
		List<Person> personListFiltered = personList.stream().distinct().collect(Collectors.toList());
		assertTrue(personListFiltered.size() == 5);
	}

}
