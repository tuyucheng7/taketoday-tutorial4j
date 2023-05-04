package cn.tuyucheng.taketoday.ejb.tutorial;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloStatefulWorldTestUnitTest {

	private HelloStatefulWorldBean statefulBean;

	@Before
	public void setup() {
		statefulBean = new HelloStatefulWorldBean();
	}

	@Test
	public void whenGetHelloWorld_thenHelloStatefulWorldIsReturned() {
		String helloWorld = statefulBean.getHelloWorld();

		assertThat(helloWorld).isEqualTo("Hello Stateful World!");
	}

	@Test
	public void whenGetHelloWorldIsCalledTwice_thenCounterIs2() {
		statefulBean.getHelloWorld();
		statefulBean.getHelloWorld();

		assertThat(statefulBean.howManyTimes()).isEqualTo(2);
	}

}
