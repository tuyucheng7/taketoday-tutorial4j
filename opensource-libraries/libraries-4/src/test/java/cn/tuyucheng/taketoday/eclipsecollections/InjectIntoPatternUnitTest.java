package cn.tuyucheng.taketoday.eclipsecollections;

import org.eclipse.collections.impl.factory.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class InjectIntoPatternUnitTest {

	@Test
	public void whenInjectInto_thenCorrect() {
		List<Integer> list = Lists.mutable.of(1, 2, 3, 4);
		int result = 5;
		for (int i = 0; i < list.size(); i++) {
			Integer v = list.get(i);
			result = result + v.intValue();
		}

		assertEquals(15, result);
	}
}
