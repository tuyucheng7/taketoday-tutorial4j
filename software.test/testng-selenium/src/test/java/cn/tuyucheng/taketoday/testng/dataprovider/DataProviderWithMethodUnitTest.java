package cn.tuyucheng.taketoday.testng.dataprovider;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;

@Slf4j
public class DataProviderWithMethodUnitTest {

	@DataProvider(name = "data-provider")
	public Object[][] dataProviderMethod(Method method) {
		return switch (method.getName()) {
			case "test_add_twoNum" -> new Object[][]{{2, 3, 5}, {5, 7, 12}};
			case "test_diff_twoNum" -> new Object[][]{{2, 3, -1}, {5, 7, -2}};
			default -> null;
		};
	}

	@Test(dataProvider = "data-provider")
	public void test_add_twoNum(int a, int b, int result) {
		int sum = a + b;
		assertEquals(sum, result);
	}

	@Test(dataProvider = "data-provider")
	public void test_diff_twoNum(int a, int b, int result) {
		int diff = a - b;
		assertEquals(diff, result);
	}
}