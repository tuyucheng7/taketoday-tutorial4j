package cn.tuyucheng.taketoday.commons.lang3.test;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicThreadFactoryUnitTest {

	@Test
	public void givenBasicThreadFactoryInstance_whenCalledBuilder_thenCorrect() {
		BasicThreadFactory factory = new BasicThreadFactory.Builder()
			.namingPattern("workerthread-%d")
			.daemon(true)
			.priority(Thread.MAX_PRIORITY)
			.build();
		assertThat(factory).isInstanceOf(BasicThreadFactory.class);
	}
}
