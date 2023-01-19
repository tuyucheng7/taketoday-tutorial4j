package cn.tuyucheng.taketoday.testng.executeorder;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class ExecutedPriorityTest {

	@Test(priority = 1)
	public void b_method() {
		LOGGER.info("B method");
	}

	@Test(priority = 1)
	public void a_method() {
		LOGGER.info("A method");
	}

	@Test
	public void d_method() {
		LOGGER.info("D method");
	}

	@Test
	public void c_method() {
		LOGGER.info("C method");
	}
}