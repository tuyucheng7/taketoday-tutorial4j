package cn.tuyucheng.taketoday.maven.plugins;

import org.junit.Test;

import static org.junit.Assert.assertNull;

public class DataUnitTest {
	@Test
	public void whenDataObjectIsNotCreated_thenItIsNull() {
		Data data = null;
		assertNull(data);
	}
}