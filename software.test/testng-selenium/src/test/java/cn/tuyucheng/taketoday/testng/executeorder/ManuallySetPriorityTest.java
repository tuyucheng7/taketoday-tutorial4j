package cn.tuyucheng.taketoday.testng.executeorder;

import org.testng.annotations.Test;

public class ManuallySetPriorityTest {


	@Test(priority = 2) // Second-Highest Priority
	public void a_test() {
	}

	@Test(priority = 3) // Lowest Priority
	public void c_test() {
	}

	@Test(priority = 1) // Highest Priority
	public void b_test() {
	}
}