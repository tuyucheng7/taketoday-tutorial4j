package cn.tuyucheng.taketoday.test.cdi2observers.tests;

import cn.tuyucheng.taketoday.cdi2observers.services.TextService;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TextServiceUnitTest {

	@Test
	public void givenTextServiceInstance_whenCalledparseText_thenCorrect() {
		TextService textService = new TextService();
		assertThat(textService.parseText("Tuyucheng")).isEqualTo("TUYUCHENG");
	}
}
