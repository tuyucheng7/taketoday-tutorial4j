package cn.tuyucheng.taketoday.ejb.tutorial;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloStatelessWorldTestUnitTest {

	private HelloStatelessWorldBean statelessBean;

	@Before
	public void setup() {
		statelessBean = new HelloStatelessWorldBean();
	}

	@Test
	public void whenGetHelloWorld_thenHelloStatelessWorldIsReturned() {
		String helloWorld = statelessBean.getHelloWorld();

		assertThat(helloWorld).isEqualTo("Hello Stateless World!");
	}

}
