package cn.tuyucheng.taketoday.logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = {"classpath:springAop-applicationContext.xml"})
public class CalculatorIntegrationTest {

	@Autowired
	private SampleAdder sampleAdder;

	@Test
	public void whenAddValidValues_returnsSuccessfully() {
		final int addedValue = sampleAdder.add(12, 12);

		assertEquals(24, addedValue);
	}

	@Test(expected = IllegalArgumentException.class)
	public void whenAddInValidValues_throwsException() {
		sampleAdder.add(12, -12);
	}
}