package cn.tuyucheng.taketoday.testng.dataprovider;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class MultipleParameterUnitTest {

	@DataProvider(name = "data-provider")
	public Object[][] dpMethod() {
		return new Object[][]{{2, 3, 5}, {5, 7, 12}};
	}

	@Test(dataProvider = "data-provider")
	public void shouldUsingMultiplePar(int a, int b, int result) {
		int sum = a + b;
		assertEquals(result, sum);
	}
}