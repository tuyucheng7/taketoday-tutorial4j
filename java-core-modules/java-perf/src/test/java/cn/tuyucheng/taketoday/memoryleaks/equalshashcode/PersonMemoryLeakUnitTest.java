package cn.tuyucheng.taketoday.memoryleaks.equalshashcode;

import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class PersonMemoryLeakUnitTest {
	@Test
	@Ignore // Test deliberately ignored as memory leak tests consume lots of resources
	public void givenMap_whenEqualsAndHashCodeNotOverridden_thenMemoryLeak() {
		Map<Person, Integer> map = new HashMap<Person, Integer>();
		for (int i = 0; i < 10000000; i++) {
			map.put(new Person("jon"), 1);
		}
		assertTrue(map.size() > 1);
		System.out.print("Debug Point - VisuaLVM");
	}

	@Test
	@Ignore // Test deliberately ignored as memory leak tests consume lots of resources
	public void givenMap_whenEqualsAndHashCodeOverridden_thenNoMemoryLeak() {
		Map<PersonOptimized, Integer> map = new HashMap<PersonOptimized, Integer>();
		for (int i = 0; i < 10000; i++) {
			map.put(new PersonOptimized("jon"), 1);
		}
		assertTrue(map.size() == 1);
		System.out.print("Debug Point - VisuaLVM");
	}
}
